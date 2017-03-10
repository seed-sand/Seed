package seed.web;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import seed.domain.Objective;
import seed.domain.ObjectiveList;
import seed.exception.ResourceNotFoundException;
import seed.exception.UnauthenticatedException;
import seed.exception.UnauthorizedException;
import seed.repository.ObjectiveListRepository;
import seed.repository.ObjectiveRepository;
import seed.repository.UserRepository;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.web.bind.annotation.RequestMethod.*;

/**
 * Created by Macsnow on 2017/3/4.
 */
@RestController
@RequestMapping("/objectiveList")
public class ObjectiveListController {
    private final ObjectiveListRepository objectiveListRepository;
    private final ObjectiveRepository objectiveRepository;
    private final UserRepository userRepository;

    @Autowired
    ObjectiveListController(ObjectiveListRepository objectiveListRepository, UserRepository userRepository, ObjectiveRepository objectiveRepository) {
        this.objectiveListRepository = objectiveListRepository;
        this.userRepository = userRepository;
        this.objectiveRepository = objectiveRepository;
    }

    @RequestMapping(method = POST)
    ResponseEntity<?> create(@RequestBody @Valid ObjectiveList objectiveList, HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    objectiveList.setUserId(userId);
                    List<ObjectId> objectIdLists = Optional.ofNullable(user.getObjectiveListCreated()).orElse(new ArrayList<>());
                    ObjectiveList objectiveList1 = objectiveListRepository.insert(objectiveList);
                    objectIdLists.add(objectiveList1.getId());
                    user.setObjectiveListCreated(objectIdLists);
                    userRepository.save(user);
                    return new ResponseEntity<>(objectiveList1, HttpStatus.CREATED);
                })
                .orElseThrow(UnauthenticatedException::new);
    }

    @RequestMapping(method = DELETE, value = "/{objectiveListId}")
    ResponseEntity<?> delete(@PathVariable ObjectId objectiveListId, HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    ObjectiveList objectiveList1 = objectiveListRepository.findById(objectiveListId)
                            .orElseThrow(() -> new ResourceNotFoundException(objectiveListId,
                                    "objective list"));
                    return Optional.of(objectiveList1)
                            .filter(objectiveList2 -> objectiveList2.getUserId().equals(userId))
                            .map(objectiveList -> {
                                List<ObjectId> objectIdLists = Optional.ofNullable(user.getObjectiveListCreated()).orElse(new ArrayList<>());
                                objectIdLists = objectIdLists.stream()
                                        .filter(objectId -> objectId != objectiveListId)
                                        .collect(Collectors.toList());
                                user.setObjectiveListCreated(objectIdLists);
                                userRepository.save(user);
                                objectiveListRepository.delete(objectiveList);
                                return ResponseEntity.noContent().build();
                            })
                            .orElseThrow(() -> new ResourceNotFoundException(objectiveListId,
                                    "objective list"));
                })
                .orElseThrow(UnauthenticatedException::new);

    }

    @RequestMapping(method = PUT, value = "/{objectiveListId}")
    ResponseEntity<?> update(@PathVariable ObjectId objectiveListId,
                             @RequestBody @Valid ObjectiveList objectiveList,
                             HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    ObjectiveList objectiveList1 = objectiveListRepository.findById(objectiveListId)
                            .orElseThrow(() -> new ResourceNotFoundException(objectiveListId,
                            "objective list"));
                    return Optional.of(objectiveList1)
                            .filter(objectiveList2 -> objectiveList2.getUserId().equals(userId))
                            .map(objectiveList2 -> {
                                objectiveList.setId(objectiveListId);
                                return new ResponseEntity<>(objectiveListRepository.save(objectiveList), HttpStatus.OK);
                            })
                            .orElseThrow(UnauthorizedException::new);
                })
                .orElseThrow(UnauthenticatedException::new);
    }

    @RequestMapping(method = GET, value = "/{objectiveListId}")
    ResponseEntity<?> getProfile(@PathVariable ObjectId objectiveListId) {
        return objectiveListRepository.findById(objectiveListId)
                .map(objectiveList -> new ResponseEntity<>(objectiveList, HttpStatus.OK))
                .orElseThrow(() -> new ResourceNotFoundException(objectiveListId,
                        "objective list"));
    }

    @RequestMapping(method = GET)
    ResponseEntity<?> getObjectiveLists(HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    List<ObjectId> objectiveLists = Optional.ofNullable(user.getObjectiveListCreated())
                            .orElse(new ArrayList<>());
                    return new ResponseEntity<>(objectiveLists, HttpStatus.OK);
                })
                .orElseThrow(UnauthenticatedException::new);
    }

    @RequestMapping(method = PATCH, value = "/{objectiveListId}/objective")
    ResponseEntity<?> pushObjective(@PathVariable ObjectId objectiveListId,
                                    @RequestBody ObjectId objectiveId,
                                    HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    ObjectiveList objectiveList = objectiveListRepository.findById(objectiveListId)
                            .orElseThrow(() -> new ResourceNotFoundException(objectiveListId,
                                    "objective list"));
                    return Optional.of(objectiveList)
                            .filter(objectiveList2 -> objectiveList2.getUserId().equals(userId))
                            .map(objectiveList1 -> {
                                List<ObjectId> objectives = Optional.ofNullable(objectiveList1.getObjectives()).orElse(new ArrayList<>());
                                Objective objective = objectiveRepository.findById(objectiveId)
                                        .orElseThrow(() -> new ResourceNotFoundException(objectiveId, "objective"));
                                objective.setListId(objectiveListId);
                                objectives.add(objectiveRepository.save(objective).getId());
                                objectives = objectives.stream().distinct().collect(Collectors.toList());
                                objectiveList1.setObjectives(objectives);
                                return new ResponseEntity<>(objectiveListRepository.save(objectiveList1), HttpStatus.OK);
                            })
                            .orElseThrow(UnauthorizedException::new);
                })
                .orElseThrow(UnauthenticatedException::new);
    }

    @RequestMapping(method = DELETE, value = "/{objectiveListId}/objective")
    ResponseEntity<?> popObjective(@PathVariable ObjectId objectiveListId,
                                   @RequestBody ObjectId objectiveId,
                                   HttpSession httpSession) {
        ObjectId userId = (ObjectId) httpSession.getAttribute("userId");
        return userRepository.findById(userId)
                .map(user -> {
                    ObjectiveList objectiveList = objectiveListRepository.findById(objectiveListId)
                            .orElseThrow(() -> new ResourceNotFoundException(objectiveListId,
                                    "objective list"));
                    return Optional.of(objectiveList)
                            .filter(objectiveList2 -> objectiveList2.getUserId().equals(userId))
                            .map(objectiveList1 -> {
                                List<ObjectId> objectives = Optional.ofNullable(objectiveList1.getObjectives()).orElse(new ArrayList<>());
                                Objective objective = objectiveRepository.findById(objectiveId)
                                        .orElseThrow(() -> new ResourceNotFoundException(objectiveId, "objective"));
                                objective.setListId(null);
                                objectiveRepository.save(objective);
                                objectives = objectives.stream()
                                        .filter(objectId -> objectId != objectiveId)
                                        .collect(Collectors.toList());
                                objectiveList1.setObjectives(objectives);
                                return ResponseEntity.noContent().build();
                            })
                            .orElseThrow(UnauthorizedException::new);
                })
                .orElseThrow(UnauthenticatedException::new);
    }
}
