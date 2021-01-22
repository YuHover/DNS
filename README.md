### 功能说明

- 接收Client DNS请求，拦截非法请求（本地文件dnsrelay.txt中定义非法请求域名）
- 接收Client DNS请求，处理合法请求（本地文件dnsrelay.txt中定义合法请求域名）
- 接收Client DNS请求，转发给上层DNS服务器，接收上层应答，回复给Client



### 配置参数说明

- 主线程接收Client DNS请求，交由HandleRequestThread线程处理
- HandleRequestThread构造的应答包或者由上层DNS服务器返回的应答包由1个线程回复给Client

- **HANDLE_THREAD_NUM**  处理DNS请求的线程数

- **SEND_SENIOR_THREAD_NUM** 无法处理的请求发送给上层DNS服务器的线程数
- **RECEIVE_SENIOR_THREAD_NUM** 接收上层DNS服务器应答的线程数(SEND_SENIOR_THREAD_NUM * RECEIVE_SENIOR_THREAD_NUM)
- **LOCAL_FILE_NAME** 本地文件名称(路径)
- **ILLEGAL_IP** 非法IP，本地文件默认为0.0.0.0
- **SENIOR_DNS_IP** 上层DNS服务器IP地址

