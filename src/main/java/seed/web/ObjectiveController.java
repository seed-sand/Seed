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
import seed.domain.Comment;
import seed.domain.Event;
import seed.domain.Objective;
import seed.exception.InvalidFieldException;
import seed.exception.ResourceNotFoundException;
import seed.exception.UnauthenticatedException;
import seed.exception.UnauthorizedException;
import seed.repository.CommentRepository;
import seed.repository.EventRepository;
import seed.repository.ObjectiveRepository;
import seed.repository.UserRepository;

import javax.servlet.http.HttpSession;
import javax.swing.text.html.Option;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;
import static seed.util.Encryption.encrypt;

/**
 * Created by Froggy
 * 2017-03-02.
 */

@RestController
@RequestMapping("/objective")
public class ObjectiveController {
    private final UserRepository userRepository;
    private final ObjectiveRepository objectiveRepository;
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;

    @Autowired
    public ObjectiveController(UserRepository userRepository, ObjectiveRepository objectiveRepository, CommentRepository commentRepository, EventRepository eventRepository){
        this.userRepository = userRepository;
        this.objectiveRepository = objectiveRepository;
        this.commentRepository = commentRepository;
        this.eventRepository = eventRepository;
    }

    @RequestMapping(method = POST)
    ResponseEntity<?> creat(@RequestBody @Valid Objective objective, HttpSession httpSession){
        ObjectId userId = (ObjectId)httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map( user -> {
                    objective.setUserId(userId);
                    Objective objective1 = objectiveRepository.insert(objective);
                    List<ObjectId> objectiveCreated = Optional.ofNullable(user.getObjectiveCreated()).orElse(new ArrayList<>());
                    objectiveCreated.add(objective.getId());
                    user.setObjectiveCreated(objectiveCreated);
                    userRepository.save(user);
                    return new ResponseEntity<>(objective1, HttpStatus.CREATED);
                })
                .orElseThrow(UnauthorizedException::new);
    }

    @RequestMapping(method = DELETE, value = "/{objectiveId}")
    ResponseEntity<?> delete(@PathVariable ObjectId objectiveId, HttpSession httpSession){
        ObjectId userId = (ObjectId)httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    Objective objective1 = objectiveRepository.findById(objectiveId)
                            .orElseThrow(() -> new ResourceNotFoundException(objectiveId,"objectiveId"));
                    return Optional.of(objective1)
                            .filter(objective2 -> objective2.getUserId().equals(userId))
                            .map(objective -> {
                                objectiveRepository.delete(objective);
                                List<ObjectId> objectiveCreated = Optional.ofNullable(user.getObjectiveCreated()).orElse(new ArrayList<>());
                                objectiveCreated = objectiveCreated.stream()
                                        .filter(userId1 -> userId1 != userId).collect(Collectors.toList());
                                user.setObjectiveCreated(objectiveCreated);
                                userRepository.save(user);
                                //返回一个没有正文的响应
                                return ResponseEntity.noContent().build();
                            }).orElseThrow(UnauthorizedException::new);
                }).orElseThrow(() -> new ResourceNotFoundException(userId,"userId"));
    }

    @RequestMapping(method = PUT, value = "/{objectiveId}")
    ResponseEntity<?> update(@PathVariable ObjectId objectiveId,
                             @RequestBody @Valid Objective objective,
                             HttpSession httpSession){
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        //通过userId找到用户，再将用户map()为目标
        return userRepository.findById(userId)
                .map(user -> {
                    //通过目标id创建该目标,找不到Id则Optional槽为null，报错
                    Objective objective1 = objectiveRepository.findById(objectiveId)
                            .orElseThrow(() -> new ResourceNotFoundException(objectiveId, "objective"));

                    return Optional.of(objective1)
                            //判断目标的用户id和当前session的用户id是否相同。
                            .filter(objective2 -> objective2.getUserId().equals(userId))
                            .map(objective2 -> {
                                objective.setId(objectiveId);
                                objective.setUserId(userId);
                                objective.setListId(objective2.getListId());
                                //更新目标
                                return new ResponseEntity<>(objectiveRepository.save(objective), HttpStatus.OK);
                            })
                            //若不同objective1被过滤，Optional槽为null，发生错误
                            .orElseThrow(UnauthorizedException::new);
                //若找不到用户则Optional槽为null，报错。
                }).orElseThrow(() -> new ResourceNotFoundException(userId, "userId"));
    }

    @RequestMapping(method = GET, value = "/{objectiveId}")
    ResponseEntity<?> getProfile(@PathVariable ObjectId objectiveId){
        return objectiveRepository.findById(objectiveId).map(objective -> new ResponseEntity<>(objective, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException(objectiveId, "objectiveId"));
    }

    @RequestMapping(method = GET)
    ResponseEntity<?> getObjectives(HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    List<ObjectId> objectives = Optional.ofNullable(user.getObjectiveCreated())
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

    @RequestMapping(method = GET, value = "/{objectiveId}/assignment")
    ResponseEntity<?> share(@PathVariable ObjectId objectiveId){
        return objectiveRepository.findById(objectiveId)
                .map(objective -> {
                    String encryptedObjectiveId = encrypt("SHA", objectiveId.toHexString());
                    return new ResponseEntity<>(encryptedObjectiveId, HttpStatus.OK);
                })
                .orElseThrow(() -> new ResourceNotFoundException(objectiveId, "objectiveId"));
    }

    @RequestMapping(method = PATCH, value = "/{objectiveId}/assignment")
    ResponseEntity<?> join(@PathVariable ObjectId objectiveId, HttpSession httpSession){
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId).map(user -> {
            //将userId放到objective下
            Objective objective = objectiveRepository.findById(objectiveId)
                    .map(objective0 -> {
                        List<ObjectId> participant = Optional.ofNullable(objective0.getAssignment()).orElse(new ArrayList<>());
                        participant.add(userId);
                        participant = participant.stream().distinct().collect(Collectors.toList());
                        objective0.setAssignment(participant);
                        return objective0;
                    }).orElseThrow(() -> new ResourceNotFoundException(objectiveId,"objectiveId"));
            //将objectiveId 放到user下
            List<ObjectId> objectiveJoined = Optional.ofNullable(user.getObjectiveJoined()).orElse(new ArrayList<>());
            objectiveJoined.add(objectiveId);
            objectiveJoined = objectiveJoined.stream().distinct().collect(Collectors.toList());
            user.setObjectiveJoined(objectiveJoined);
            userRepository.save(user);
            return new ResponseEntity<>(objectiveRepository.save(objective), HttpStatus.OK);
        }).orElseThrow(() -> new ResourceNotFoundException(userId,"userId"));
    }

    @RequestMapping(method = DELETE, value = "/{objectiveId}/assignment")
    ResponseEntity<?> leave(@PathVariable ObjectId objectiveId, HttpSession httpSession){
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId).map(user -> {
            //将objective下的userId移除
            Objective objective = objectiveRepository.findById(objectiveId).map(objective1 -> {
                List<ObjectId> participant = Optional.ofNullable(objective1.getAssignment()).orElse(new ArrayList<>());
                participant = participant.stream().filter(id -> id != userId).collect(Collectors.toList());
                objective1.setAssignment(participant);
                return objective1;
            }).orElseThrow(() -> new ResourceNotFoundException(objectiveId,"objectiveId"));
            //将user下的objectiveId移除
            List<ObjectId> objectiveJoined = Optional.ofNullable(user.getObjectiveJoined()).orElse(new ArrayList<>());
            objectiveJoined = objectiveJoined.stream().filter(id -> id != objectiveId).collect(Collectors.toList());
            user.setObjectiveJoined(objectiveJoined);
            userRepository.save(user);
            return ResponseEntity.noContent().build();
        }).orElseThrow(() -> new ResourceNotFoundException(userId,"userId"));
    }

    @RequestMapping(method = POST, value = "/{objectiveId}/comment")
    ResponseEntity<?> comment(@PathVariable ObjectId objectiveId,
                              @RequestBody @Valid Comment comment,
                              HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    Objective objective = objectiveRepository.findById(objectiveId)
                            .orElseThrow(() -> new ResourceNotFoundException(objectiveId, "objective"));
                    return Optional.of(objective)
                            .filter(objective1 -> objective1.getUserId().equals(userId))
                            .map(objective1 -> {
                                List<ObjectId> comments = Optional.ofNullable(objective1.getComments())
                                        .orElse(new ArrayList<>());
                                comment.setUserId(userId);
                                comment.setObjectiveId(objectiveId);
                                comments.add(commentRepository.insert(comment).getId());
                                objective1.setComments(comments);
                                return new ResponseEntity<>(objectiveRepository.save(objective1), HttpStatus.CREATED);
                            })
                            .orElseThrow(UnauthorizedException::new);
                })
                .orElseThrow(UnauthenticatedException::new);
    }

    @RequestMapping(method = GET, value = "/{objectiveId}/comment")
    ResponseEntity<?> getComments(@PathVariable ObjectId objectiveId,
                                  @RequestParam(value = "page", defaultValue = "0") int page,
                                  @RequestParam(value = "size", defaultValue = "7") int size,
                                  @RequestParam(value = "sort", defaultValue = "ASC") Sort.Direction direction,
                                  HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    Objective objective = objectiveRepository.findById(objectiveId)
                            .orElseThrow(() -> new ResourceNotFoundException(objectiveId, "objective"));
                    return Optional.of(objective)
                            .filter(objective1 -> objective1.getUserId().equals(userId))
                            .map(objective1 -> new ResponseEntity<>(commentRepository.findByObjectiveId(objective1.getId(),
                                    new PageRequest(page,
                                            size,
                                            new Sort(direction, "id"))), HttpStatus.OK))
                            .orElseThrow(UnauthorizedException::new);
                })
                .orElseThrow(UnauthenticatedException::new);
    }

    @RequestMapping(method = DELETE, value = "/{objectiveId}/comment/{commentId}")
    ResponseEntity<?> deleteComments(@PathVariable ObjectId objectiveId,
                                     @PathVariable ObjectId commentId,
                                     HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    Objective objective = objectiveRepository.findById(objectiveId)
                            .orElseThrow(() -> new ResourceNotFoundException(objectiveId, "objective"));
                    return Optional.of(objective)
                            .filter(objective1 -> objective1.getUserId().equals(userId))
                            .map(objective1 -> {
                                commentRepository.delete(commentId);
                                List<ObjectId> comments = Optional.ofNullable(objective1.getEvents())
                                        .orElse(new ArrayList<>());
                                comments = comments
                                        .stream()
                                        .filter(objectId -> objectId != commentId)
                                        .collect(Collectors.toList());
                                objective1.setComments(comments);
                                objectiveRepository.save(objective1);
                                //TODO: 这里需要捕获异常吗？
                                return ResponseEntity.noContent().build();
                            })
                            .orElseThrow(UnauthorizedException::new);
                })
                .orElseThrow(UnauthenticatedException::new);
    }

    @RequestMapping(method = POST, value = "/{objectiveId}/event")
    ResponseEntity<?> event(@PathVariable ObjectId objectiveId,
                            @RequestBody @Valid Event event,
                            HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    Objective objective = objectiveRepository.findById(objectiveId)
                            .orElseThrow(() -> new ResourceNotFoundException(objectiveId, "objective"));
                    return Optional.of(objective)
                            .filter(objective1 -> objective1.getUserId().equals(userId))
                            .map(objective1 -> {
                                List<ObjectId> events = Optional.ofNullable(objective1.getEvents())
                                        .orElse(new ArrayList<>());
                                event.setObjectiveId(objectiveId);
                                events.add(eventRepository.insert(event).getId());
                                objective1.setEvents(events);
                                return new ResponseEntity<>(objectiveRepository.save(objective1), HttpStatus.CREATED);
                            })
                            .orElseThrow(UnauthorizedException::new);
                })
                .orElseThrow(UnauthenticatedException::new);
    }

    @RequestMapping(method = GET, value = "/{objectiveId}/event")
    ResponseEntity<?> getEvents(@PathVariable ObjectId objectiveId,
                                @RequestParam(value = "page", defaultValue = "0") int page,
                                @RequestParam(value = "size", defaultValue = "7") int size,
                                @RequestParam(value = "sort", defaultValue = "ASC") Sort.Direction direction,
                                HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    Objective objective = objectiveRepository.findById(objectiveId)
                            .orElseThrow(() -> new ResourceNotFoundException(objectiveId, "objective"));
                    return Optional.of(objective)
                            .filter(objective1 -> objective1.getUserId().equals(userId))
                            .map(objective1 -> new ResponseEntity<>(eventRepository.findByObjectiveId(objective1.getId(),
                                    new PageRequest(page,
                                            size,
                                            new Sort(direction, "id"))), HttpStatus.OK))
                            .orElseThrow(UnauthorizedException::new);
                })
                .orElseThrow(UnauthenticatedException::new);
    }

    @RequestMapping(method = GET, value = "/{objectiveId}/event/{eventId}")
    ResponseEntity<?> getEvent(@PathVariable ObjectId objectiveId,
                               @PathVariable ObjectId eventId,
                               HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    Objective objective = objectiveRepository.findById(objectiveId)
                            .orElseThrow(() -> new ResourceNotFoundException(objectiveId, "objective"));
                    return Optional.of(objective)
                            .filter(objective1 -> objective1.getUserId().equals(userId))
                            .filter(objective1 -> objective1.getEvents().contains(eventId))
                            .map(objective1 -> new ResponseEntity<>(eventRepository.findById(eventId), HttpStatus.OK))
                            .orElseThrow(UnauthorizedException::new);
                })
                .orElseThrow(UnauthenticatedException::new);
    }

    @RequestMapping(method = PUT, value = "/{objectiveId}/event/{eventId}")
    ResponseEntity<?> updateEvent(@PathVariable ObjectId objectiveId,
                                  @PathVariable ObjectId eventId,
                                  @RequestBody Event event,
                                  HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    Objective objective = objectiveRepository.findById(objectiveId)
                            .orElseThrow(() -> new ResourceNotFoundException(objectiveId, "objective"));
                    return Optional.of(objective)
                            .filter(objective1 -> objective1.getUserId().equals(userId))
                            .filter(objective1 -> objective1.getEvents().contains(eventId))
                            .map(objective1 -> {
                                event.setObjectiveId(objectiveId);
                                return new ResponseEntity<>(eventRepository.save(event), HttpStatus.OK);
                            })
                            .orElseThrow(UnauthorizedException::new);
                })
                .orElseThrow(UnauthenticatedException::new);
    }

    @RequestMapping(method = DELETE, value = "/{objectiveId}/event/{eventId}")
    ResponseEntity<?> deleteEvent(@PathVariable ObjectId objectiveId,
                                  @PathVariable ObjectId eventId,
                                  HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    Objective objective = objectiveRepository.findById(objectiveId)
                            .orElseThrow(() -> new ResourceNotFoundException(objectiveId, "objective"));
                    return Optional.of(objective)
                            .filter(objective1 -> objective1.getUserId().equals(userId))
                            .filter(objective1 -> objective1.getEvents().contains(eventId))
                            .map(objective1 -> {
                                eventRepository.delete(eventId);
                                List<ObjectId> events = Optional.ofNullable(objective1.getEvents())
                                        .orElse(new ArrayList<>());
                                events = events.stream()
                                        .filter(objectId -> objectId != eventId)
                                        .collect(Collectors.toList());
                                objective1.setEvents(events);
                                objectiveRepository.save(objective1);
                                return ResponseEntity.noContent().build();
                            })
                            .orElseThrow(UnauthorizedException::new);
                })
                .orElseThrow(UnauthenticatedException::new);
    }

}
