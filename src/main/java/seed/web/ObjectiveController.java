package seed.web;

import org.bson.types.ObjectId;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import seed.domain.Objective;
import seed.domain.User;
import seed.exception.UnauthorizedException;
import seed.repository.ObjectiveRepository;
import seed.repository.UserRepository;

import java.util.HashSet;
import java.util.List;

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
    public ObjectiveController(MongoTemplate mongoTemplate,UserRepository userRepository,ObjectiveRepository objectiveRepository){
        this.mongoTemplate = mongoTemplate;
        this.userRepository = userRepository;
        this.objectiveRepository = objectiveRepository;
    }

    @RequestMapping(method = RequestMethod.POST)
    Objective creat(@RequestBody Objective objective){
        return objectiveRepository.insert(objective);
    }

    @RequestMapping(method = RequestMethod.PATCH)
    Objective modify(@RequestBody ObjectId id, @RequestBody ObjectId userId, @RequestBody ObjectId listId,
                         @RequestBody String title, @RequestBody String description, @RequestBody DateTime deadline,
                         @RequestBody int priority,@RequestBody boolean status){
        Objective objective = objectiveRepository.findById(id);
        if(userId != objective.getUserId()){
            throw new UnauthorizedException();
        }else{
            objective.setTitle(title);
            objective.setDescription(description);
            objective.setDeadline(deadline);
            objective.setPriority(priority);
            objective.setStatus(status);
            return objectiveRepository.save(objective);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE)
    void delete(@RequestBody ObjectId id,@RequestBody ObjectId userId){
        Objective objective = objectiveRepository.findById(id);
        if(userId != objective.getUserId()){
            throw new UnauthorizedException();
        }else{
            objectiveRepository.delete(objective);
        }
    }

    //三个get还没写

    @RequestMapping(method = RequestMethod.PATCH)
    //这里objectiveId是被分享目标ID，userId是想加入该目标用户的ID
    Objective join(@RequestBody ObjectId objectiveId,@RequestBody ObjectId userId){
        Objective objective = objectiveRepository.findById(objectiveId);
        List<ObjectId> newAssignment = objective.getAssignment();
        newAssignment.add(userId);

        //去重操作；
        HashSet hashSet = new HashSet(newAssignment);
        newAssignment.clear();
        newAssignment.addAll(hashSet);

        objective.setAssignment(newAssignment);
        return objectiveRepository.insert(objective);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    Objective leave(@RequestBody ObjectId objectiveId,@RequestBody ObjectId userId){
        Objective objective = objectiveRepository.findById(objectiveId);
        List<ObjectId> list = objective.getAssignment();
        int i = list.indexOf(userId);
        if(i==-1){
            throw new UnauthorizedException();
        }else{
            list.remove(i);
        }
        objective.setAssignment(list);
        return objectiveRepository.save(objective);
    }
}
