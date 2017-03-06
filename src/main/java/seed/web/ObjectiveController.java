package seed.web;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seed.domain.Objective;
import seed.exception.InvalidFieldException;
import seed.exception.ResourceNotFoundException;
import seed.exception.UnauthenticatedException;
import seed.exception.UnauthorizedException;
import seed.repository.ObjectiveRepository;
import seed.repository.UserRepository;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by Froggy
 * 2017-03-02.
 */

@RestController
@RequestMapping("/objective")
public class ObjectiveController {
    private final UserRepository userRepository;
    private final ObjectiveRepository objectiveRepository;

    @Autowired
    public ObjectiveController(UserRepository userRepository, ObjectiveRepository objectiveRepository){
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

    @RequestMapping(method = GET, value = "/{ObjectiveId}")
    ResponseEntity<?> getProfile(@PathVariable ObjectId objectiveId){
        return objectiveRepository.findById(objectiveId).map(objective -> new ResponseEntity<>(objective, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException(objectiveId, "objectiveId"));
    }

    @RequestMapping(method = GET)
    ResponseEntity<?> getObjectives(HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    List<ObjectId> objectives = Optional.ofNullable(user.getObjectiveListCreated())
                            .orElse(new ArrayList<>());
                    return new ResponseEntity<>(objectives, HttpStatus.OK);
                })
                .orElseThrow(UnauthenticatedException::new);
    }

    @RequestMapping(method = GET, value = "/search")
    ResponseEntity<?> search(@RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "7") int size,
                             @RequestParam(value = "sort", defaultValue = "ASC") Sort.Direction direction ,
                             @RequestParam(value = "key", defaultValue = "title") String key,
                             @RequestParam(value = "value") String value,
                             HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    Sort sort = new Sort(direction, key);
                    Pageable pageable = new PageRequest(page, size, sort);
                    List<Objective> objectives;
                    switch (key) {
                        case "title":
                            objectives = objectiveRepository.findByTitleIgnoreCase(value, pageable);
                            break;
                        case "deadLine":
                            objectives = objectiveRepository.findByDeadline(DateTime.parse(value), pageable);
                            break;
                        case "priority":
                            objectives = objectiveRepository.findByPriority(Integer.parseInt(value), pageable);
                            break;
                        case "status":
                            objectives = objectiveRepository.findByStatus(Boolean.parseBoolean(value), pageable);
                            break;
                        default:
                            throw new InvalidFieldException(key);
                    }
                    objectives = objectives.stream()
                            .filter(objective -> objective.getUserId().equals(userId))
                            .collect(Collectors.toList());
                    return new ResponseEntity<>(objectives, HttpStatus.OK);
                })
                .orElseThrow(UnauthenticatedException::new);
    }

    //我不知道这个方法写的对不对@Michale  --Froggy
    @RequestMapping(method = GET, value = "/{ObjectiveId}/assignment")
    ResponseEntity<?> share(@PathVariable ObjectId objectiveId){
        return objectiveRepository.findById(objectiveId).map(objective -> new ResponseEntity<>(objective, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException(objectiveId, "objectiveId"));
    }

    @RequestMapping(method = PATCH, value = "/{ObjectiveId}/assignment")
    ResponseEntity<?> join (@PathVariable ObjectId objectiveId, HttpSession httpSession){
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId).map(user -> {
            //将userId放到objective下
            Objective objective = objectiveRepository.findById(objectiveId)
                    .map(objective0 -> {
                        List<ObjectId> participant = objective0.getAssignment();
                        participant.add(userId);
                        participant.stream().distinct().collect(Collectors.toList());
                        objective0.setAssignment(participant);
                        return objective0;
                    }).orElseThrow(() -> new ResourceNotFoundException(objectiveId,"objectiveId"));
            //将objectiveId 放到user下
            List<ObjectId> objectiveJoined = user.getObjectiveJoined();
            objectiveJoined.add(objectiveId);
            objectiveJoined.stream().distinct().collect(Collectors.toList());
            user.setObjectiveJoined(objectiveJoined);
            userRepository.save(user);
            return new ResponseEntity<>(objectiveRepository.save(objective), HttpStatus.OK);
        }).orElseThrow(() -> new ResourceNotFoundException(userId,"userId"));
    }

    @RequestMapping(method = DELETE,value = "/{ObjectiveId}/assignment")
    ResponseEntity<?> leave (@PathVariable ObjectId objectiveId, HttpSession httpSession){
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId).map(user -> {
            //将objective下的userId移除
            Objective objective = objectiveRepository.findById(objectiveId).map(objective1 -> {
                List<ObjectId> participant = objective1.getAssignment();
                participant.stream().filter(id -> id != userId).collect(Collectors.toList());
                objective1.setAssignment(participant);
                return objective1;
            }).orElseThrow(() -> new ResourceNotFoundException(objectiveId,"objectiveId"));
            //将user下的objectiveId移除
            List<ObjectId> objectiveJoined = user.getObjectiveJoined();
            objectiveJoined.stream().filter(id -> id != objectiveId).collect(Collectors.toList());
            user.setObjectiveJoined(objectiveJoined);
            userRepository.save(user);
            return new ResponseEntity<>(objectiveRepository.save(objective), HttpStatus.OK);
        }).orElseThrow(() -> new ResourceNotFoundException(userId,"userId"));
    }
}
