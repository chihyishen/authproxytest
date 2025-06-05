package backend.authproxytest.controller;

//import backend.authproxytest.dao.TestDao;
//import backend.authproxytest.repo.TestRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/testApi")
@CrossOrigin( origins = "https://testfrontend-532457043033.asia-east1.run.app")
@RequiredArgsConstructor
public class TestController {
    @GetMapping
    public ResponseEntity<String> findAll() {
        return ResponseEntity.ok( "Test API is working!");
    }
//    private final TestRepo repo;
//
//    @GetMapping
//    public ResponseEntity<List<TestDao>> findAll() {
//        return ResponseEntity.ok(repo.findAll());
//    }
//
//    @DeleteMapping("/{rq}")
//    public ResponseEntity<Void> DeleteAccount(@PathVariable Integer rq) {
//        long id = (long)rq;
//        repo.deleteById(id);
//        return ResponseEntity.ok().build();
//    }
//
//    @PostMapping
//    public ResponseEntity<TestDao> CreateAccount(@RequestBody TestDao rq) {
//        TestDao saved = repo.save(rq);
//        return ResponseEntity.ok(saved);
//    }
}
