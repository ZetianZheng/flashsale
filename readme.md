## Flash Sale System
Flash Sale System requires optimization for high concurrency and large traffic. Ensured high availability when managers start activities and upload items, users to place orders.
- Designed a flash sale system based on the REST F by Spring Boot
- Increased TPS and enhanced availability in high concurrency by using Nginx, Redis, and RocketMQ, compared to the plan which is a no-cache and synchronous solution
- Identified the oversells problem and solved it by utilizing Lua and the TCC transaction, which kept the consistency between cache and database
- Accomplished flow control and circuit-breaking by using Sentinel to guarantee the reliability of microservices
