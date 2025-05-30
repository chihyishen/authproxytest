package backend.authproxytest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.Socket;
import java.sql.DriverManager;
import java.util.*;

@RestController
@RequestMapping("/")
@CrossOrigin("*")
@RequiredArgsConstructor
public class HealthController {
    @GetMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("✅ Application is running! Auth Proxy test ready.");
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/test-proxy")
    public ResponseEntity<String> testProxy() {
        try {
            // 測試 127.0.0.1:5432 是否有在監聽
            Socket socket = new Socket();
            socket.connect(new java.net.InetSocketAddress("127.0.0.1", 5432), 5000);
            socket.close();
            return ResponseEntity.ok("✅ Auth Proxy is listening on 127.0.0.1:5432");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("❌ Auth Proxy not listening: " + e.getMessage());
        }
    }
    @GetMapping("/detailed-debug")
    public ResponseEntity<Map<String, Object>> detailedDebug() {
        Map<String, Object> debug = new HashMap<>();

        // 檢查進程
        try {
            ProcessBuilder pb = new ProcessBuilder("ps", "aux");
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder processes = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("proxy") || line.contains("sql") || line.contains("cloud")) {
                    processes.append(line).append("\n");
                }
            }
            debug.put("relevant_processes", processes.toString());
        } catch (Exception e) {
            debug.put("processes_error", e.getMessage());
        }

        // 檢查網路連線
        try {
            ProcessBuilder pb = new ProcessBuilder("netstat", "-ln");
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder netstat = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("5432") || line.contains("3306")) {
                    netstat.append(line).append("\n");
                }
            }
            debug.put("listening_ports", netstat.toString());
        } catch (Exception e) {
            debug.put("netstat_error", e.getMessage());
        }

        // 檢查 cloudsql 相關檔案
        Map<String, String> files = new HashMap<>();
        String[] paths = {
                "/cloudsql",
                "/tmp/cloudsql",
                "/var/run/cloudsql",
                "/etc/cloudsql"
        };

        for (String path : paths) {
            File dir = new File(path);
            if (dir.exists()) {
                files.put(path, "EXISTS - " + Arrays.toString(dir.list()));
            } else {
                files.put(path, "NOT_EXISTS");
            }
        }
        debug.put("cloudsql_paths", files);

        return ResponseEntity.ok(debug);
    }
    @GetMapping("/force-sql-connection")
    public ResponseEntity<String> forceSqlConnection() {
        try {
            // 嘗試直接建立 JDBC 連線，這可能會觸發 Auth Proxy
            String url = "jdbc:postgresql://127.0.0.1:5432/job_db";
            DriverManager.getConnection(url, "postgres", "dummy");
            return ResponseEntity.ok("Connection successful");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Connection failed: " + e.getMessage());
        }
    }
}

