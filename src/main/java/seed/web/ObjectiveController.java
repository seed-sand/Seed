package seed.web;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seed.domain.Objective;
import seed.domain.User;
import seed.exception.ResourceNotFoundException;
import seed.exception.UnauthorizedException;
import seed.repository.ObjectiveRepository;
import seed.repository.UserRepository;

import javax.servlet.http.HttpSession;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.PATCH;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by Froggy
 * 2017-03-02.
 */

@RestController
@RequestMapping("/objective")
public class ObjectiveController {
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepository;
    private final ObjectiveRepository objectiveRepository;

    @Autowired
    public ObjectiveController(MongoTemplate mongoTemplate, UserRepository userRepository, ObjectiveRepository objectiveRepository){
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
        this.objectiveRepository = objectiveRepository;
    }

    @RequestMapping(method = POST)
    ResponseEntity<?> creat(@RequestBody Objective objective, HttpSession httpSession){
        ObjectId userId = (ObjectId)httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map( user ->{
                    objective.setUserId(userId);
                    Objective objective1 = objectiveRepository.insert(objective);
                    List<ObjectId> objectiveCreated = user.getObjectiveCreated();
                   objectiveCreated.add(objective.getId());
                    user.setObjectiveCreated(objectiveCreated);
                    userRepository.save(user);
                    return new ResponseEntity<>(objective1, HttpStatus.CREATED);
                })
                .orElseThrow(UnauthorizedException::new);
    }

    @RequestMapping(method = PATCH, value = "/{ObjectiveId}")
    ResponseEntity<?> modify(@PathVariable ObjectId id,
                             @RequestBody Objective objective,
                             HttpSession httpSession){
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        //通过userId找到用户，再将用户map()为目标
        return userRepository.findById(userId)
                .map(user -> {
                    //通过目标id创建该目标,找不到Id则Optional槽为null，报错
                    Objective objective1 = objectiveRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException(id, "objective"));

                    return Optional.of(objective1)
                            //判断目标的用户id和当前session的用户id是否相同。
                            .filter(objective2 -> objective2.getUserId().equals(userId))
                            .map(objective2 -> {
                                objective.setId(objective1.getId());

                                //更新目标
                                return new ResponseEntity<>(objectiveRepository.save(objective), HttpStatus.OK);
                            })
                            //若不同objective1被过滤，Optional槽为null，发生错误
                            .orElseThrow(UnauthorizedException::new);
                //若找不到用户则Optional槽为null，报错。
                }).orElseThrow(() -> new ResourceNotFoundException(userId, "userId"));
    }

    @RequestMapping(method = DELETE, value = "/{ObjectiveId}")
    ResponseEntity<?> delete(@PathVariable ObjectId id,HttpSession httpSession){
        ObjectId userId = (ObjectId)httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user ->{
                    Objective objective1 = objectiveRepository.findById(id)
                            .orElseThrow(() -> new ResourceNotFoundException(id,"objectiveId"));
                    return Optional.of(objective1)
                            .filter(objective2 -> objective2.getUserId().equals(userId))
                            .map(objective -> {
                                objectiveRepository.delete(objective);
                                List<ObjectId> objectiveCreated = user.getObjectiveCreated();
                                objectiveCreated.stream()
                                        .filter(userId1 -> userId1 != userId).collect(Collectors.toList());
                                user.setObjectiveCreated(objectiveCreated);
                                userRepository.save(user);
                                //返回一个没有正文的响应
                                return ResponseEntity.noContent().build();
                            }).orElseThrow(UnauthorizedException::new);
                }).orElseThrow(() -> new ResourceNotFoundException(userId,"userId"));
    }

    //三个get还没写

    //Hi,Michale.You need to complete this method by encryption
    @RequestMapping(method = PATCH, value = "/{ObjectiveId}/assignment")
    //这里objectiveId是被分享目标ID，userId是想加入该目标用户的ID
    ResponseEntity<?> join(@PathVariable ObjectId objectiveId, HttpSession httpSession){
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return objectiveRepository.findById(objectiveId)
                .map(objective -> {
                    List<ObjectId> participant = objective.getAssignment();
                    participant.add(userId);
                    //去重操作
                    participant.stream().distinct().collect(Collectors.toList());
                    objective.setAssignment(participant);
                    User user = userRepository.findById(userId)
                            .map(user0 -> {
                                List<ObjectId> objective1 = user0.getObjectiveJoined();
                                objective1.add(objectiveId);
                                objective1.stream().distinct().collect(Collectors.toList());
                                user0.setObjectiveCreated(objective1);
                                return  user0;
                            }).orElseThrow(() -> new ResourceNotFoundException(userId,"userId"));
                    userRepository.save(user);
                    return new ResponseEntity<>(objectiveRepository.save(objective), HttpStatus.OK);
                }).orElseThrow(() -> new ResourceNotFoundException(objectiveId,"objectiveId"));
    }

    @RequestMapping(method = DELETE, value = "/{ObjectiveId}/assignment")
    ResponseEntity<?> leave(@PathVariable ObjectId objectiveId,HttpSession httpSession){
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return objectiveRepository.findById(objectiveId)
                .map(objective -> {
                    List<ObjectId> participant = objective.getAssignment()
                    .stream().filter(participantId -> participantId != userId).collect(Collectors.toList());
                    objective.setAssignment(participant);
                    User user = userRepository.findById(userId)
                            .map(user1 -> {
                                List<ObjectId> objectiveJoined1 = objective.getAssignment()
                                        .stream().filter(joinedId -> joinedId != objectiveId).collect(Collectors.toList());
                                user1.setObjectiveJoined(objectiveJoined1);
                                return user1;
                            }).orElseThrow(() -> new ResourceNotFoundException(userId,"userId"));
                    userRepository.save(user);
                    return new ResponseEntity<>(objectiveRepository.save(objective), HttpStatus.OK);
                }).orElseThrow(() -> new ResourceNotFoundException(objectiveId, "objectiveId"));
    }
}
