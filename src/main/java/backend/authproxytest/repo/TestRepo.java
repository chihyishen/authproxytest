package backend.authproxytest.repo;

import backend.authproxytest.dao.TestDao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepo extends JpaRepository<TestDao,Long> {
}
