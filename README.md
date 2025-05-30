# Cloud Run 使用 Auth Proxy 連接 Cloud SQL

這個專案示範如何在 Cloud Run 中使用自管理的 Cloud SQL Auth Proxy 連接到 PostgreSQL 資料庫。

## 🔑 核心概念

**在 Docker 容器內啟動 Auth Proxy**，而不是依賴 Cloud Run 的自動 Cloud SQL 連線功能。

## 📁 專案結構

```
├── src/                    # Spring Boot 應用程式代碼
├── Dockerfile             # 包含 Auth Proxy 安裝的容器配置
├── start.sh               # Auth Proxy 啟動腳本
├── application.yaml       # Spring Boot 配置
└── pom.xml               # Maven 依賴
```

## 🐳 Docker 關鍵配置

### Dockerfile 要點
- 安裝 `cloud_sql_proxy`
- 複製 `start.sh` 啟動腳本
- 使用 `start.sh` 作為容器入口點

### start.sh 腳本作用
1. 先啟動 Cloud SQL Proxy
2. 等待 127.0.0.1:5432 可用
3. 再啟動 Spring Boot 應用程式

## ⚙️ Cloud Run Console 設定

### ❌ 不要勾選的設定
- **Connect to Cloud SQL**: 取消勾選（我們自己管理 Auth Proxy）

### ✅ 需要的設定
- **VPC**: 如果使用 VPC，egress 設為「指轉送私人 IP」
- **Service Account**: 需要有 `Cloud SQL Client` 權限
- **Environment Variables**:
    - `INSTANCE_CONNECTION_NAME`: `project:region:instance`
    - `SPRING_DATASOURCE_URL`: `jdbc:postgresql://127.0.0.1:5432/database_name`
    - `DB_USER`: 資料庫使用者
- **Secrets**:
    - `DB_PASSWORD`: 從 Secret Manager 讀取

## 🚀 部署指令

```bash
# 部署到 Cloud Run（不使用自動 Cloud SQL 連線）
gcloud run deploy your-service \
    --image your-image-url \
    --service-account your-service-account \
    --set-env-vars="INSTANCE_CONNECTION_NAME=project:region:instance,SPRING_DATASOURCE_URL=jdbc:postgresql://127.0.0.1:5432/database" \
    --set-secrets="DB_PASSWORD=secret-name:latest" \
    --region asia-east1
    # 注意：沒有 --add-cloudsql-instances
```

## ✅ 成功驗證

部署後在 logs 中應該看到：
```
=== Manual Auth Proxy Startup ===
⏳ Attempt 1/60...
✅ Auth Proxy is ready!
=== Starting Application ===
```

## 🔧 故障排除

- **Auth Proxy 啟動失敗**: 檢查 `INSTANCE_CONNECTION_NAME` 格式和服務帳戶權限
- **連線被拒絕**: 確認 Cloud SQL Admin API 已啟用
- **啟動超時**: 增加 Cloud Run 的啟動超時設定

## 💡 為什麼這樣做？

Cloud Run 的自動 Cloud SQL 連線功能有時會有啟動問題，自管理 Auth Proxy 提供：
- ✅ 更好的控制權
- ✅ 清楚的除錯資訊
- ✅ 穩定的啟動順序
- ✅ 真正的 Auth Proxy 連線（IAM + 加密）
