package com.example.data.repository

import com.example.data.local.BookmarkedTipEntity
import com.example.data.local.CompletedLessonEntity
import com.example.data.local.QuizScoreEntity
import com.example.data.local.UserProgressDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

class AceRepository(private val progressDao: UserProgressDao) {

    val completedLessonIds: Flow<List<String>> = progressDao.getCompletedLessonIds()
    val bookmarkedTips: Flow<List<BookmarkedTipEntity>> = progressDao.getAllBookmarks()
    val quizScores: Flow<List<QuizScoreEntity>> = progressDao.getQuizScores()

    suspend fun toggleLessonCompletion(lessonId: String, isCompleted: Boolean) {
        if (isCompleted) {
            progressDao.markLessonCompleted(CompletedLessonEntity(lessonId))
        } else {
            progressDao.unmarkLessonCompleted(lessonId)
        }
    }

    suspend fun toggleBookmark(id: String, lessonTitle: String, tipText: String, isBookmarked: Boolean) {
        if (isBookmarked) {
            progressDao.addBookmark(BookmarkedTipEntity(id = id, lessonTitle = lessonTitle, tipText = tipText))
        } else {
            progressDao.deleteBookmark(id)
        }
    }

    suspend fun recordQuizResult(score: Int, totalQuestions: Int) {
        progressDao.recordQuizScore(QuizScoreEntity(score = score, totalQuestions = totalQuestions))
    }

    fun getModules(): List<AceModule> = listOf(
        AceModule(
            id = "mod_intro",
            title = "1. Cloud Core & Environment Setup",
            sectionNumber = "Section 1",
            examWeight = "~20% of ACE Exam",
            summary = "Master cloud fundamentals, GCP resource hierarchy, IAM access control, and billing management.",
            iconName = "cloud",
            lessons = listOf(
                AceLesson(
                    id = "les_1_1",
                    title = "What is Cloud Computing & ACE Role?",
                    subtitle = "Understanding on-demand infrastructure & the ACE responsibility scope.",
                    readingTimeMinutes = 4,
                    contentSections = listOf(
                        LessonSection(
                            heading = "What is Cloud Computing?",
                            bodyParagraphs = listOf(
                                "Cloud computing is the on-demand delivery of compute power, storage, networking, and software over the internet with pay-as-you-go pricing. Instead of purchasing physical servers in a data center, you rent virtualized hardware managed by Google.",
                                "In the Google Cloud Associate Cloud Engineer (ACE) exam, Cloud is defined as a scalable, highly available, software-defined infrastructure environment."
                            ),
                            codeOrConceptSnippet = "Physical Data Center  -->  Virtualized GCP Services\n[ Buy Hardware & Space ]      [ Provision On-Demand in Seconds ]"
                        ),
                        LessonSection(
                            heading = "The Shared Responsibility Model",
                            bodyParagraphs = listOf(
                                "Google manages physical infrastructure, data center security, hardware maintenance, and hypervisors.",
                                "You (the Cloud Engineer) are responsible for configuring access policies (IAM), guest operating systems, application code, data encryption, and network firewall rules."
                            ),
                            tableRows = listOf(
                                "Google Responsibilities" to "Physical Data Centers, Hardware Maintenance, Hypervisors, Physical Security",
                                "Customer Responsibilities" to "Data Security, IAM Access, Application Code, OS Patching (IaaS), Firewall Rules"
                            )
                        )
                    ),
                    keyTakeaways = listOf(
                        "Cloud replaces upfront CapEx (Capital Expenditure) with predictable OpEx (Operational Expenditure).",
                        "The Associate Cloud Engineer deploys, monitors, and operates solutions on Google Cloud."
                    ),
                    aceExamTips = listOf(
                        "ACE Tip: On IaaS (Compute Engine), you patch the OS. On PaaS/Serverless (Cloud Run, App Engine), Google manages the runtime OS!"
                    )
                ),
                AceLesson(
                    id = "les_1_2",
                    title = "GCP Resource Hierarchy",
                    subtitle = "Organization > Folders > Projects > Resources",
                    readingTimeMinutes = 5,
                    contentSections = listOf(
                        LessonSection(
                            heading = "The 4-Tier Resource Structure",
                            bodyParagraphs = listOf(
                                "Google Cloud organizes all resources in a strict four-level hierarchy. Policy inheritance flows top-down: permissions granted at a higher level automatically apply to all child levels.",
                                "1. Organization: The root node representing your enterprise domain (e.g. company.com).\n2. Folders: Organizational units to group projects by department (e.g., Engineering, Finance) or environment (e.g., Prod, Dev).\n3. Projects: The mandatory container for all GCP resources. Billing and APIs are enabled at the project level.\n4. Resources: The actual GCP services (e.g., Compute Engine VM instances, Cloud Storage buckets)."
                            ),
                            codeOrConceptSnippet = "Organization (company.com)\n ├── Folder: Engineering\n │    ├── Project: Dev-App (VMs, Buckets)\n │    └── Project: Prod-App (VMs, Buckets)\n └── Folder: Finance\n      └── Project: Payroll (BigQuery)"
                        )
                    ),
                    keyTakeaways = listOf(
                        "Projects are the fundamental billing & permission boundary in GCP.",
                        "Permissions inherit downwards: Granting an IAM role at the Folder level gives access to all Projects inside that folder."
                    ),
                    aceExamTips = listOf(
                        "ACE Exam Rule: You CANNOT create a resource (VM, Bucket) without a Project! Every resource belongs to exactly one Project."
                    )
                ),
                AceLesson(
                    id = "les_1_3",
                    title = "IAM & Service Accounts",
                    subtitle = "Managing members, roles, and machine identities.",
                    readingTimeMinutes = 5,
                    contentSections = listOf(
                        LessonSection(
                            heading = "Identity and Access Management (IAM)",
                            bodyParagraphs = listOf(
                                "IAM controls WHO (identity) has WHAT ACCESS (role) to WHICH RESOURCE. Always follow the Principle of Least Privilege: grant only the minimum permissions required.",
                                "Role Types in GCP:\n• Primitive Roles: Viewer, Editor, Owner (Broad, outdated; avoid in production!).\n• Predefined Roles: Granular roles curated by Google (e.g., Compute Instance Admin, Storage Object Viewer).\n• Custom Roles: User-defined combinations of specific permissions when predefined roles are too broad."
                            )
                        ),
                        LessonSection(
                            heading = "Service Accounts",
                            bodyParagraphs = listOf(
                                "A Service Account is a special account used by applications or workloads (like Compute Engine VMs) rather than human users, allowing machines to make authenticated API requests securely."
                            ),
                            codeOrConceptSnippet = "VM Instance  --[ Uses Service Account ]-->  Accesses Cloud Storage Bucket"
                        )
                    ),
                    keyTakeaways = listOf(
                        "Prefer Predefined Roles over Primitive (Owner/Editor/Viewer) roles.",
                        "Service Accounts allow applications on VMs to authenticate securely without hardcoded API keys."
                    ),
                    aceExamTips = listOf(
                        "ACE Tip: When a VM needs to read from a Cloud Storage bucket, attach a Service Account with 'roles/storage.objectViewer' to the VM instance."
                    )
                )
            )
        ),
        AceModule(
            id = "mod_compute",
            title = "2. Compute Engine Infrastructure",
            sectionNumber = "Section 2.1 & 3.1",
            examWeight = "~30% of ACE Exam",
            summary = "Deep dive into Compute Engine VMs, machine families, persistent disks, spot instances, and autoscaling MIGs.",
            iconName = "dns",
            lessons = listOf(
                AceLesson(
                    id = "les_2_1",
                    title = "Compute Engine Virtual Machines",
                    subtitle = "Machine types, Spot VMs, and workload optimization.",
                    readingTimeMinutes = 6,
                    contentSections = listOf(
                        LessonSection(
                            heading = "Compute Engine Overview",
                            bodyParagraphs = listOf(
                                "Compute Engine provides Infrastructure as a Service (IaaS), delivering customizable Virtual Machines (VMs) running Linux or Windows on Google's physical servers.",
                                "Machine Families:\n• General-Purpose (E2, N2, N2D, C3D): Balanced price and performance for web servers, dev environments, and databases.\n• Compute-Optimized (C2, C2D): High CPU performance per core for gaming servers, high-performance computing (HPC), and media rendering.\n• Memory-Optimized (M1, M2, M3): Massive RAM (up to 12TB) for large in-memory databases like SAP HANA.\n• Accelerator-Optimized (A2, G2): Attached GPUs/TPUs for Machine Learning training and AI inference."
                            )
                        ),
                        LessonSection(
                            heading = "Standard vs Spot VMs (Preemptible)",
                            bodyParagraphs = listOf(
                                "Spot VMs use excess Google capacity at steep discounts (60–91% cheaper). However, Google can reclaim/terminate a Spot VM at any time with a 30-second warning.",
                                "Spot VMs are ideal for fault-tolerant, batch-processing workloads (e.g., rendering, data analytics pipelines) where jobs can easily resume if interrupted."
                            ),
                            tableRows = listOf(
                                "Standard VMs" to "100% availability guarantee, ideal for stateful web apps & databases.",
                                "Spot VMs" to "60-91% savings, subject to preemption, ideal for batch jobs & fault-tolerant worker pools."
                            )
                        )
                    ),
                    keyTakeaways = listOf(
                        "Select machine families based on bottleneck: CPU (C2), RAM (M2), GPU (A2), or Balanced (N2/E2).",
                        "Spot VMs provide massive cost savings for stateless, interruptible batch jobs."
                    ),
                    aceExamTips = listOf(
                        "ACE Exam Scenario: If a question asks for the most cost-effective solution for non-critical batch processing, choose Spot VMs (or a Managed Instance Group with Spot VMs)!"
                    )
                ),
                AceLesson(
                    id = "les_2_2",
                    title = "Compute Engine Storage Options",
                    subtitle = "Persistent Disks, Local SSDs, and Hyperdisks.",
                    readingTimeMinutes = 5,
                    contentSections = listOf(
                        LessonSection(
                            heading = "Attaching Block Storage to VMs",
                            bodyParagraphs = listOf(
                                "Compute Engine VMs require block storage for OS boot drives and application data. GCP offers several storage options tailored for speed, redundancy, and durability:",
                                "1. Standard Persistent Disk (pd-standard): Efficient, low-cost HDD storage for sequential I/O and backups.\n2. Balanced Persistent Disk (pd-balanced): Cost-effective SSD storage for general enterprise workloads.\n3. SSD Persistent Disk (pd-ssd): High IOPS for latency-sensitive databases (e.g. MySQL, PostgreSQL).\n4. Regional Persistent Disk: Replicates data synchronously across TWO zones in the same region for high availability disaster recovery.\n5. Local SSD: Physically attached directly to the host server running the VM. Delivers ultra-high IOPS and sub-millisecond latency, but is EPHEMERAL (data is lost when the VM stops/terminates!)."
                            ),
                            tableRows = listOf(
                                "Zonal Persistent Disk" to "Network-attached SSD/HDD within a single zone. Snapshots provide backup.",
                                "Regional Persistent Disk" to "Synchronous 2-zone replication for HA failover without application-level replication.",
                                "Local SSD" to "Extreme speed (direct NVMe), ephemeral storage. Data lost on VM stop!"
                            )
                        )
                    ),
                    keyTakeaways = listOf(
                        "Persistent Disks are network-attached durable storage that persist independently of VM lifecycle.",
                        "Local SSDs offer highest IOPS but are ephemeral — never store persistent database data solely on Local SSD!"
                    ),
                    aceExamTips = listOf(
                        "ACE Exam Rule: Regional Persistent Disk is the recommended solution for high availability boot or data disks requiring synchronous cross-zone protection!"
                    )
                ),
                AceLesson(
                    id = "les_2_3",
                    title = "Managed Instance Groups (MIGs) & Autoscaling",
                    subtitle = "High availability, load balancing, and automated VM repair.",
                    readingTimeMinutes = 6,
                    contentSections = listOf(
                        LessonSection(
                            heading = "What is a Managed Instance Group (MIG)?",
                            bodyParagraphs = listOf(
                                "A Managed Instance Group (MIG) operates a collection of identical Virtual Machines created from a single Instance Template.",
                                "Key Capabilities of MIGs:\n• Autoscaling: Automatically adds or removes VM instances based on CPU utilization, HTTP load, or Cloud Monitoring metrics.\n• Autohealing: Uses health checks to monitor application health. If a VM fails the health check, the MIG automatically recreates it.\n• High Availability: Regional MIGs distribute VM instances across multiple zones within a region."
                            ),
                            codeOrConceptSnippet = "Instance Template  -->  MIG (Autohealing + Autoscaling across Zone A, B, C)  <-- Load Balancer"
                        )
                    ),
                    keyTakeaways = listOf(
                        "Instance Templates define VM configuration (image, machine type, disks, startup script).",
                        "Autohealing requires a Health Check. Unhealthy instances are recreated automatically."
                    ),
                    aceExamTips = listOf(
                        "ACE Exam Tip: To update software on a MIG without downtime, use a Rolling Update with a new Instance Template!"
                    )
                )
            )
        ),
        AceModule(
            id = "mod_storage",
            title = "3. Cloud Storage & Data Solutions",
            sectionNumber = "Section 2.2 & 3.2",
            examWeight = "~30% of ACE Exam",
            summary = "Understand Cloud Storage classes, object lifecycle rules, redundancy, and database selection.",
            iconName = "storage",
            lessons = listOf(
                AceLesson(
                    id = "les_3_1",
                    title = "Cloud Storage Classes & Buckets",
                    subtitle = "Standard, Nearline, Coldline, Archive storage.",
                    readingTimeMinutes = 6,
                    contentSections = listOf(
                        LessonSection(
                            heading = "What is Cloud Storage?",
                            bodyParagraphs = listOf(
                                "Cloud Storage is GCP's object storage service for unstructured data (images, videos, backups, logs, static website assets). Data is stored as Objects inside Buckets.",
                                "Buckets have a globally unique name across all of Google Cloud."
                            )
                        ),
                        LessonSection(
                            heading = "The 4 Storage Classes",
                            bodyParagraphs = listOf(
                                "Choose a storage class based on access frequency and data retention policy:",
                                "1. Standard Storage: For 'hot' data accessed frequently (websites, active streaming, analytics). No minimum retention period.\n2. Nearline Storage: For data accessed at most once per month (monthly reports, recent backups). 30-day minimum storage duration.\n3. Coldline Storage: For data accessed at most once per quarter (disaster recovery, legal archives). 90-day minimum storage duration.\n4. Archive Storage: For data accessed less than once per year (long-term compliance archives). 365-day minimum storage duration; lowest storage cost, highest access/retrieval cost."
                            ),
                            tableRows = listOf(
                                "Standard" to "Frequent access, highest storage cost, zero retrieval fee.",
                                "Nearline" to "Access < 1x/month, 30-day minimum duration.",
                                "Coldline" to "Access < 1x/quarter, 90-day minimum duration.",
                                "Archive" to "Access < 1x/year, 365-day minimum duration, lowest storage cost."
                            )
                        )
                    ),
                    keyTakeaways = listOf(
                        "All storage classes offer high durability (99.999999999% / 11 nines).",
                        "Retrieving data from Nearline/Coldline/Archive incurs an early deletion or retrieval fee if accessed before the minimum retention duration."
                    ),
                    aceExamTips = listOf(
                        "ACE Rule: If data is stored for compliance and rarely accessed, use Archive Storage. If data is accessed monthly, use Nearline!"
                    )
                ),
                AceLesson(
                    id = "les_3_2",
                    title = "Object Lifecycle Management & Security",
                    subtitle = "Automating data transitions, CMEK encryption, and bucket access.",
                    readingTimeMinutes = 5,
                    contentSections = listOf(
                        LessonSection(
                            heading = "Object Lifecycle Management",
                            bodyParagraphs = listOf(
                                "Object Lifecycle Management allows you to define rules that automatically transition objects to a colder storage class or delete them based on conditions (e.g., age in days, creation date, version status).",
                                "Example Rule: Transition objects from Standard to Coldline after 30 days, and delete them after 365 days."
                            )
                        ),
                        LessonSection(
                            heading = "Encryption & Bucket Access",
                            bodyParagraphs = listOf(
                                "• Encryption at Rest: ALL data in Cloud Storage is encrypted by default using Google-managed keys.",
                                "• CMEK (Customer-Managed Encryption Keys): Use Cloud KMS if your security team must control key rotation and revocation.",
                                "• Uniform Bucket-Level Access: Disables object-specific ACLs and enforces IAM permissions at the bucket level (Google recommended best practice!)."
                            )
                        )
                    ),
                    keyTakeaways = listOf(
                        "Lifecycle management saves costs by moving aging data to colder storage tiers automatically.",
                        "Enforce Uniform Bucket-Level Access for simpler, consistent IAM security."
                    ),
                    aceExamTips = listOf(
                        "ACE Exam Scenario: To automatically reduce storage costs for log files older than 60 days, configure an Object Lifecycle Management rule!"
                    )
                ),
                AceLesson(
                    id = "les_3_3",
                    title = "Selecting Google Cloud Databases",
                    subtitle = "Cloud SQL, Spanner, Firestore, BigQuery, Bigtable.",
                    readingTimeMinutes = 6,
                    contentSections = listOf(
                        LessonSection(
                            heading = "Database Product Decision Guide",
                            bodyParagraphs = listOf(
                                "The ACE exam frequently tests selecting the right GCP database service based on workload requirements:",
                                "1. Cloud SQL: Managed relational database (MySQL, PostgreSQL, SQL Server) for standard web apps and OLTP transactions up to 64TB.",
                                "2. Cloud Spanner: Fully managed, globally distributed relational database with ACID compliance and horizontal scaling for enterprise global apps.",
                                "3. Firestore: Serverless NoSQL document database for mobile and web apps with real-time syncing.",
                                "4. Cloud Bigtable: NoSQL wide-column store for ultra-high throughput key-value data (IoT telemetry, time-series analytics).",
                                "5. BigQuery: Serverless, highly scalable data warehouse for complex analytical SQL queries (OLAP) across petabytes of data."
                            ),
                            tableRows = listOf(
                                "Cloud SQL" to "Relational OLTP (MySQL/PostgreSQL), single-region/regional failover.",
                                "Cloud Spanner" to "Global Relational ACID, massive scale.",
                                "Firestore" to "NoSQL Document, mobile/web app state.",
                                "BigQuery" to "Data Warehouse OLAP, SQL analytics on petabytes."
                            )
                        )
                    ),
                    keyTakeaways = listOf(
                        "Match database type to access pattern: Transactional (Cloud SQL/Spanner) vs Analytical (BigQuery) vs NoSQL (Firestore/Bigtable).",
                        "BigQuery is for analytics queries, NOT transactional OLTP web backends."
                    ),
                    aceExamTips = listOf(
                        "ACE Shortcut: If the question mentions 'relational database + MySQL/PostgreSQL', choose Cloud SQL. If it mentions 'global ACID scale', choose Spanner!"
                    )
                )
            )
        ),
        AceModule(
            id = "mod_serverless_pubsub",
            title = "4. Messaging, Serverless & Containers",
            sectionNumber = "Section 2.3 & 3.3",
            examWeight = "~25% of ACE Exam",
            summary = "Master Cloud Pub/Sub asynchronous messaging, Cloud Run microservices, Cloud Functions, and GKE cluster management.",
            iconName = "dns",
            lessons = listOf(
                AceLesson(
                    id = "les_4_1",
                    title = "Cloud Pub/Sub & Event Messaging",
                    subtitle = "Asynchronous decoupling, Topics, Subscriptions & Dead Letter Queues.",
                    readingTimeMinutes = 6,
                    contentSections = listOf(
                        LessonSection(
                            heading = "What is Cloud Pub/Sub?",
                            bodyParagraphs = listOf(
                                "Cloud Pub/Sub is a fully managed, real-time asynchronous messaging service that decouples event producers (publishers) from event consumers (subscribers).",
                                "Core Concepts:\n• Publisher: Sends messages to a Pub/Sub Topic.\n• Topic: A named feed to which publishers send messages.\n• Subscription: Represents the stream of messages from a specific topic to be delivered to a subscriber.\n• Push Subscription: Pub/Sub initiates an HTTP POST request to a web endpoint (e.g., Cloud Run or App Engine) when a message arrives.\n• Pull Subscription: The subscriber application periodically queries Pub/Sub for new messages."
                            ),
                            codeOrConceptSnippet = "Publisher (IoT Device / Web Service)\n  │\n  ▼  Publish Message\n[ Pub/Sub Topic: order-created ]\n  ├── [ Subscription A (Push) ] ──> Cloud Run (Inventory Service)\n  └── [ Subscription B (Pull) ] ──> GKE Worker Pool (Email Notification)"
                        ),
                        LessonSection(
                            heading = "When to Use Cloud Pub/Sub & Real-World Examples",
                            bodyParagraphs = listOf(
                                "Real-World Examples:\n1. E-Commerce Order Processing: Decouple the user checkout API from downstream slow operations (sending email receipts, updating inventory, generating shipping labels).\n2. IoT Fleet Telemetry Ingestion: Thousands of smart meters stream vehicle metrics into a Pub/Sub Topic, which feeds Dataflow and Bigtable.\n3. Event-Driven Microservices: Asynchronous communication between decoupled services without hardcoded API addresses.",
                                "When to Use Pub/Sub:\n• Need to buffer traffic spikes and prevent overwhelming backend systems.\n• Need Fan-Out messaging (one event consumed by multiple distinct services).\n• Need asynchronous, loosely coupled microservices architecture."
                            ),
                            tableRows = listOf(
                                "Use Pub/Sub When" to "Asynchronous decoupling, event streaming, buffering high-volume traffic, fan-out pattern.",
                                "Do NOT Use Pub/Sub When" to "Synchronous request-response is required (use direct HTTP REST/gRPC APIs instead)."
                            )
                        )
                    ),
                    keyTakeaways = listOf(
                        "Pub/Sub guarantees At-Least-Once delivery. Subscribers must be idempotent.",
                        "Push subscriptions work well with serverless endpoints (Cloud Run, Cloud Functions). Pull subscriptions suit dedicated worker pools (Compute Engine, GKE)."
                    ),
                    aceExamTips = listOf(
                        "ACE Exam Scenario: If an application experiences sudden traffic spikes that cause backend databases to crash, place Cloud Pub/Sub between the front-end and back-end to buffer requests!"
                    )
                ),
                AceLesson(
                    id = "les_4_2",
                    title = "Serverless Compute: Cloud Run vs GKE vs Cloud Functions",
                    subtitle = "Choosing the right container & event execution platform.",
                    readingTimeMinutes = 7,
                    contentSections = listOf(
                        LessonSection(
                            heading = "GCP Compute Decision Spectrum",
                            bodyParagraphs = listOf(
                                "GCP offers a spectrum of compute environments ranging from fully serverless to raw infrastructure:",
                                "1. Cloud Functions: Lightweight, single-purpose event handlers triggered by cloud events (GCS file uploads, Pub/Sub messages, Firestore mutations). Scales to 0 automatically.\n2. Cloud Run: Fully managed serverless platform for stateless containerized web applications or APIs. Supports any language/library packaged in a Docker container, scales down to 0, and charges per 100ms of execution time.\n3. Google Kubernetes Engine (GKE): Managed Kubernetes for complex, enterprise container orchestration with stateful pods, custom CRDs, fine-grained node pool hardware selection, and multi-cluster networking.\n4. App Engine: Fully managed PaaS for web apps (Standard for quick language runtimes; Flexible for custom Docker containers)."
                            ),
                            tableRows = listOf(
                                "Cloud Functions" to "Event-driven micro-tasks, zero container management, snippets triggered by GCP events.",
                                "Cloud Run" to "Stateless HTTP containers, serverless scaling to 0, cost-effective microservices.",
                                "GKE" to "Complex Kubernetes cluster, stateful sets, custom CRDs, deep networking control.",
                                "App Engine" to "PaaS web apps, integrated deployment pipeline."
                            )
                        ),
                        LessonSection(
                            heading = "When to Use Which Compute Service",
                            bodyParagraphs = listOf(
                                "Real-World Selection Examples:\n• Image Thumbnail Generator on File Upload: Use Cloud Functions (triggered instantly when an object lands in a Cloud Storage bucket).\n• REST API in Python/Go Docker Container: Use Cloud Run (easy deployment, automatically scales with HTTP traffic, zero cost when idle).\n• Microservice Mesh with 50+ Interdependent Containers & Persistent Volumes: Use GKE (provides full Kubernetes API, Helm charts, and custom pod affinity)."
                            )
                        )
                    ),
                    keyTakeaways = listOf(
                        "Cloud Run runs ANY Docker container statelessly on HTTP/gRPC.",
                        "Cloud Functions react to GCP system events; Cloud Run serves web HTTP APIs; GKE manages full Kubernetes clusters."
                    ),
                    aceExamTips = listOf(
                        "ACE Exam Rule: If the requirement is 'run a custom Docker container statelessly with zero infrastructure management and scale to zero', choose Cloud Run!"
                    )
                )
            )
        ),
        AceModule(
            id = "mod_deep_data_services",
            title = "5. Deep Dive: Managed GCP Data Services",
            sectionNumber = "Section 2.4 & 3.4",
            examWeight = "~25% of ACE Exam",
            summary = "In-depth breakdown of Cloud Bigtable, Cloud Spanner, Firestore, and BigQuery with concrete use-case scenarios.",
            iconName = "storage",
            lessons = listOf(
                AceLesson(
                    id = "les_5_1",
                    title = "Cloud Bigtable: High-Throughput NoSQL",
                    subtitle = "Sub-10ms wide-column store for IoT, telemetry, and time-series data.",
                    readingTimeMinutes = 6,
                    contentSections = listOf(
                        LessonSection(
                            heading = "What is Cloud Bigtable?",
                            bodyParagraphs = listOf(
                                "Cloud Bigtable is GCP's enterprise-grade, ultra-low latency NoSQL wide-column database. It is designed to handle massive workloads with consistent sub-10 millisecond latency and seamless scaling to petabytes.",
                                "Key Characteristics:\n• Wide-Column Store: Data is organized into rows, column families, and columns with timestamps.\n• Single Row Key Indexing: Queries are optimized solely around the Row Key. There are no secondary indexes or SQL joins.\n• High Write Throughput: Ideal for continuous high-speed data ingestion."
                            ),
                            codeOrConceptSnippet = "Row Key: vehicle_102#2026-08-03T12:00:00\n Column Family: engine_metrics\n   └── temp: 195.4 F\n   └── rpm: 2400\n Column Family: location_metrics\n   └── lat: 37.7749, lon: -122.4194"
                        ),
                        LessonSection(
                            heading = "When to Use Cloud Bigtable & Examples",
                            bodyParagraphs = listOf(
                                "Real-World Examples:\n1. Connected Vehicles & IoT Sensors: Millions of devices sending sensor readings every second.\n2. Financial Market Data: High-frequency stock price ticks and trading history.\n3. User Ad Tech Tracking: Storing real-time clickstream events and user profile signals.",
                                "When to Use Bigtable:\n• Data size exceeds 1 TB (scales efficiently up to petabytes).\n• Requires > 10,000 queries/operations per second with < 10ms latency.\n• Workload is time-series, IoT telemetry, or key-value lookup.\n• You do NOT require multi-row ACID transactions or SQL joins."
                            ),
                            tableRows = listOf(
                                "Use Bigtable When" to "Heavy streaming writes, IoT/time-series, <10ms latency, single row-key lookups, >1TB size.",
                                "Do NOT Use Bigtable When" to "Data < 1TB (too costly baseline), need SQL joins, or mobile offline sync (use Firestore/Cloud SQL instead)."
                            )
                        )
                    ),
                    keyTakeaways = listOf(
                        "Bigtable performance depends heavily on good Row Key design to avoid hotspots.",
                        "Bigtable is NOT a general relational database or a cheap small-app datastore."
                    ),
                    aceExamTips = listOf(
                        "ACE Exam Shortcut: If the question asks for a NoSQL database for IoT sensor telemetry or time-series data requiring sub-10ms latency at massive write scale, choose Cloud Bigtable!"
                    )
                ),
                AceLesson(
                    id = "les_5_2",
                    title = "Complete GCP Database Decision Matrix",
                    subtitle = "Comparing BigQuery, Spanner, Cloud SQL, Firestore & Bigtable.",
                    readingTimeMinutes = 7,
                    contentSections = listOf(
                        LessonSection(
                            heading = "Master Database Selection Matrix",
                            bodyParagraphs = listOf(
                                "Selecting the correct GCP database is one of the most heavily tested topics on the ACE exam. Memorize this decision criteria matrix:"
                            ),
                            tableRows = listOf(
                                "Cloud SQL" to "Relational (MySQL/PostgreSQL/SQL Server) • Max 64TB • Standard Web/ERP Apps • Regional OLTP",
                                "Cloud Spanner" to "Relational SQL • Unlimited Scale • Multi-Region Global ACID • Global Banking/Ticketing",
                                "Firestore" to "NoSQL Document • Mobile/Web Apps • Real-time Live Sync • Offline Mobile Cache",
                                "Cloud Bigtable" to "NoSQL Wide-Column • High Throughput (>10k QPS) • Sub-10ms Latency • IoT/Time-Series",
                                "BigQuery" to "Data Warehouse (OLAP) • SQL Analytics • Petabyte Scale • Enterprise BI & Data Science",
                                "Memorystore" to "In-Memory Cache (Redis/Memcached) • Sub-1ms Latency • Session Cache"
                            )
                        ),
                        LessonSection(
                            heading = "Exam Scenario Decision Rules",
                            bodyParagraphs = listOf(
                                "• Rule 1 (Analytics vs Transactional): Need SQL queries over historical sales reports? Choose BigQuery. Need fast row updates for user checkouts? Choose Cloud SQL or Spanner.\n• Rule 2 (Global Relational): Need strict ACID compliance across North America, Europe, and Asia simultaneously? Choose Cloud Spanner.\n• Rule 3 (Mobile Real-time Sync): Need live updates for a chat app that works offline? Choose Firestore.\n• Rule 4 (IoT Telemetry Stream): Need to ingest 50,000 sensor events/sec? Choose Cloud Bigtable."
                            )
                        )
                    ),
                    keyTakeaways = listOf(
                        "Distinguish OLTP (Cloud SQL/Spanner/Firestore) from OLAP (BigQuery).",
                        "Distinguish NoSQL Document (Firestore) from NoSQL Wide-Column (Bigtable)."
                    ),
                    aceExamTips = listOf(
                        "ACE Quick Recall: Relational Global = Spanner | Relational Local = Cloud SQL | NoSQL Realtime = Firestore | NoSQL Heavy Stream = Bigtable | SQL Analytics = BigQuery"
                    )
                )
            )
        ),
        AceModule(
            id = "mod_networking_hybrid",
            title = "6. Cloud Networking & Hybrid Interconnect",
            sectionNumber = "Section 2.5 & 3.5",
            examWeight = "~20% of ACE Exam",
            summary = "VPC Networks, Firewall Rules, Cloud VPN, Dedicated Interconnect, Partner Interconnect, and VPC Peering.",
            iconName = "cloud",
            lessons = listOf(
                AceLesson(
                    id = "les_6_1",
                    title = "Hybrid Connectivity & VPC Networking",
                    subtitle = "Connecting on-premises infrastructure to Google Cloud securely.",
                    readingTimeMinutes = 6,
                    contentSections = listOf(
                        LessonSection(
                            heading = "Connecting On-Premises to GCP",
                            bodyParagraphs = listOf(
                                "Enterprise applications often require connecting on-premises data centers to GCP VPC networks. GCP provides three primary options:",
                                "1. Cloud VPN: Secure IPsec VPN connection over the public internet. Fast to set up, encrypted, up to 3 Gbps per tunnel. Best for low bandwidth or immediate setup requirements.\n2. Dedicated Interconnect: Direct physical fiber-optic cable connection between your data center and a Google Edge facility. High bandwidth (10 Gbps or 100 Gbps), lowest latency, 99.99% SLA, data does NOT traverse public internet.\n3. Partner Interconnect: Connection through a supported service provider (e.g. Equinix, AT&T) when your data center is not co-located with a Google Edge facility. Bandwidth ranges from 50 Mbps to 10 Gbps."
                            ),
                            tableRows = listOf(
                                "Cloud VPN" to "Encrypted over Public Internet • Up to 3 Gbps • Quick setup, lowest upfront cost.",
                                "Dedicated Interconnect" to "Physical Fiber to Google • 10/100 Gbps • Maximum SLA & performance, private connection.",
                                "Partner Interconnect" to "Connecting via Service Provider • 50 Mbps to 10 Gbps • For sites without direct Google Edge presence."
                            )
                        ),
                        LessonSection(
                            heading = "VPC Peering vs Shared VPC",
                            bodyParagraphs = listOf(
                                "• VPC Network Peering: Connects two distinct VPC networks (even across different organizations) to exchange traffic privately using internal IP addresses with zero bandwidth bottleneck.\n• Shared VPC: Allows an organization to connect resources from multiple projects (Service Projects) to a common, centralized VPC network managed by a Host Project."
                            )
                        )
                    ),
                    keyTakeaways = listOf(
                        "Cloud VPN uses the public internet; Cloud Interconnect uses private dedicated lines.",
                        "Shared VPC centralizes network administration, while VPC Peering links independent networks."
                    ),
                    aceExamTips = listOf(
                        "ACE Exam Scenario: If a company needs a private >10 Gbps connection with strict 99.99% availability between their corporate data center and GCP without traversing the public internet, select Dedicated Interconnect!"
                    )
                )
            )
        )
    )

    fun getPracticeQuizQuestions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            id = 1,
            topicCategory = "Resource Hierarchy & IAM",
            questionText = "Your company wants to grant a developer team access to create and manage VMs in a specific environment without allowing them to modify billing configuration or delete the project. Where should you apply the IAM role?",
            options = listOf(
                "At the Organization level using the Owner role",
                "At the Project level using the Compute Instance Admin predefined role",
                "At the Folder level using the Primitive Editor role",
                "At the Resource level on every individual disk"
            ),
            correctOptionIndex = 1,
            explanation = "Permissions inherit top-down. Applying the 'Compute Instance Admin' predefined role at the Project level follows the principle of least privilege, allowing VM management without exposing project deletion or billing setup."
        ),
        QuizQuestion(
            id = 2,
            topicCategory = "Compute Engine",
            questionText = "You need to run a non-critical daily batch data transformation job that takes 4 hours to complete. What is the most cost-effective Compute Engine configuration?",
            options = listOf(
                "Standard N2 VM instance with automated snapshot backups",
                "Spot VM (Preemptible) instance with checkpointing to save intermediate progress",
                "Memory-Optimized M2 instance running 24/7",
                "Compute-Optimized C2 instance with Local SSD"
            ),
            correctOptionIndex = 1,
            explanation = "Spot VMs offer discounts up to 90% compared to standard instances. Because the job is non-critical batch processing, Spot VMs are the most cost-effective choice."
        ),
        QuizQuestion(
            id = 3,
            topicCategory = "Cloud Storage",
            questionText = "Your company must store audit logs for 5 years to meet regulatory compliance. The logs will almost never be accessed after creation. What Cloud Storage class and feature should you use?",
            options = listOf(
                "Standard Storage with manual monthly object migration",
                "Archive Storage with an Object Lifecycle Management policy",
                "Nearline Storage with Object Versioning disabled",
                "Coldline Storage stored on a Local SSD"
            ),
            correctOptionIndex = 1,
            explanation = "Archive Storage provides the lowest storage cost for data accessed less than once a year. Object Lifecycle Management automates retention and transitions."
        ),
        QuizQuestion(
            id = 4,
            topicCategory = "Compute Engine & Storage",
            questionText = "An application running on a Compute Engine VM requires extremely high read/write IOPS with sub-millisecond latency for temporary cache data. What disk type should you attach?",
            options = listOf(
                "Standard Persistent Disk (pd-standard)",
                "Local SSD",
                "Regional Persistent Disk (pd-ssd)",
                "Cloud Storage Bucket mounted via gcsfuse"
            ),
            correctOptionIndex = 1,
            explanation = "Local SSDs are physically attached to the server hosting the VM, delivering sub-millisecond latency and maximum IOPS for temporary cache/scratchpad data."
        ),
        QuizQuestion(
            id = 5,
            topicCategory = "Managed Instance Groups",
            questionText = "How does a Managed Instance Group (MIG) perform Autohealing when an application inside a VM becomes unresponsive?",
            options = listOf(
                "It uses CPU utilization thresholds to restart the physical host",
                "It monitors an attached HTTP/TCP Health Check and recreates the instance if it fails repeatedly",
                "It sends an email alert to the Cloud Engineer to manually restart the VM",
                "It automatically converts the VM into a Spot instance"
            ),
            correctOptionIndex = 1,
            explanation = "Autohealing in MIGs relies on a configured Health Check signal. If an instance consistently fails health probes, the MIG automatically recreates that VM."
        ),
        QuizQuestion(
            id = 6,
            topicCategory = "Cloud Databases",
            questionText = "You need a managed relational database that supports standard PostgreSQL syntax for a regional e-commerce website. Which service should you choose?",
            options = listOf(
                "BigQuery",
                "Cloud Bigtable",
                "Cloud SQL for PostgreSQL",
                "Firestore"
            ),
            correctOptionIndex = 2,
            explanation = "Cloud SQL is GCP's fully managed relational database service supporting PostgreSQL, MySQL, and SQL Server for standard OLTP workloads."
        ),
        QuizQuestion(
            id = 7,
            topicCategory = "Messaging & Pub/Sub",
            questionText = "An e-commerce web application experiences sudden flash sales where order volume spikes by 100x, causing downstream database timeouts. How should you decouple the order intake service from order processing?",
            options = listOf(
                "Use a synchronous REST API call with retry logic on Compute Engine",
                "Publish order messages to a Cloud Pub/Sub Topic and consume them asynchronously",
                "Write order records directly to BigQuery using batch SQL inserts",
                "Increase the VM instance size to M2 Memory-Optimized"
            ),
            correctOptionIndex = 1,
            explanation = "Cloud Pub/Sub acts as an asynchronous buffer between publishers and subscribers, absorbing sudden traffic spikes so downstream services can process messages at a controlled pace."
        ),
        QuizQuestion(
            id = 8,
            topicCategory = "Cloud Databases (Bigtable)",
            questionText = "You need to store and analyze real-time IoT sensor telemetry from 500,000 smart utility meters streaming continuous time-series data (>50,000 writes/sec) with sub-10ms latency. Which GCP database should you select?",
            options = listOf(
                "Cloud SQL for MySQL",
                "Cloud Bigtable",
                "Cloud Spanner",
                "Firestore"
            ),
            correctOptionIndex = 1,
            explanation = "Cloud Bigtable is a NoSQL wide-column store optimized for high-throughput, sub-10ms low-latency write workloads like IoT telemetry, time-series, and clickstreams."
        ),
        QuizQuestion(
            id = 9,
            topicCategory = "Serverless & Containers",
            questionText = "You have a stateless containerized Go REST API packaged in a Docker container. You want to host it with zero infrastructure management, automatic scaling down to 0 instances when idle, and pay per request. What service should you use?",
            options = listOf(
                "Google Kubernetes Engine (GKE) with StatefulSets",
                "Cloud Run",
                "Compute Engine VM with standard Persistent Disk",
                "App Engine Flexible Environment with manual scaling"
            ),
            correctOptionIndex = 1,
            explanation = "Cloud Run is a fully managed serverless platform for running stateless Docker containers that scales automatically from 0 to thousands of instances based on incoming HTTP traffic."
        ),
        QuizQuestion(
            id = 10,
            topicCategory = "Hybrid Connectivity",
            questionText = "A healthcare company requires a private, dedicated physical connection between their corporate data center and Google Cloud with 10 Gbps throughput and a 99.99% availability SLA without routing data over the public internet. What should they implement?",
            options = listOf(
                "Cloud VPN with multiple HA tunnels",
                "Dedicated Interconnect",
                "VPC Peering over Cloud Router",
                "Partner Interconnect with 50 Mbps bandwidth"
            ),
            correctOptionIndex = 1,
            explanation = "Dedicated Interconnect provides a direct physical fiber connection between an enterprise data center and Google's network edge with 10 Gbps / 100 Gbps pipes and high SLA without using the public internet."
        ),
        QuizQuestion(
            id = 11,
            topicCategory = "Cloud Databases (Spanner vs BigQuery)",
            questionText = "A global financial institution needs a database for international credit card transactions requiring global ACID compliance, strong consistency across multi-region deployments, and standard ANSI SQL support. Which service should they choose?",
            options = listOf(
                "BigQuery",
                "Cloud Spanner",
                "Cloud Bigtable",
                "Cloud SQL"
            ),
            correctOptionIndex = 1,
            explanation = "Cloud Spanner is GCP's globally distributed relational database offering unlimited horizontal scaling, strong multi-region ACID consistency, and standard SQL support."
        )
    )

    fun calculateVmSimulation(
        machineFamily: String,
        vCpus: Int,
        ramGb: Int,
        isSpot: Boolean,
        diskType: String,
        diskSizeGb: Int,
        isMig: Boolean
    ): VmSimulationResult {
        var baseCpuCost = vCpus * 0.0316 * 730
        var baseRamCost = ramGb * 0.0042 * 730
        
        val familyMultiplier = when (machineFamily) {
            "C2 (Compute-Optimized)" -> 1.35
            "M2 (Memory-Optimized)" -> 1.60
            "E2 (Cost-Optimized)" -> 0.80
            else -> 1.0 // N2
        }

        var totalCost = (baseCpuCost + baseRamCost) * familyMultiplier

        val diskCostPerGb = when (diskType) {
            "SSD Persistent Disk" -> 0.17
            "Balanced Persistent Disk" -> 0.10
            "Local SSD (Ephemeral)" -> 0.22
            else -> 0.04 // Standard PD
        }
        totalCost += (diskSizeGb * diskCostPerGb)

        if (isMig) {
            totalCost *= 2.0 // Assume 2 instance auto-scaling baseline
        }

        if (isSpot) {
            totalCost *= 0.30 // 70% discount
        }

        val typeName = "$machineFamily ($vCpus vCPU, ${ramGb}GB RAM)"
        val summary = if (isSpot) {
            "Spot VM configuration gives ~70% cost savings. Suitable for fault-tolerant batch workers."
        } else {
            "Standard VM configuration with 99.99% SLA availability."
        }

        val useCases = mutableListOf<String>()
        if (machineFamily.contains("C2")) useCases.add("High performance CPU rendering & HPC simulations")
        if (machineFamily.contains("M2")) useCases.add("Large in-memory databases (SAP HANA)")
        if (machineFamily.contains("E2") || machineFamily.contains("N2")) useCases.add("Web app backends & microservices")
        if (isSpot) useCases.add("Batch analytics & asynchronous worker queues")
        if (isMig) useCases.add("Autoscaling production web services with load balancing")

        return VmSimulationResult(
            machineType = typeName,
            spotDiscount = isSpot,
            estimatedMonthlyCost = Math.round(totalCost * 100.0) / 100.0,
            recommendationSummary = summary,
            bestWorkloadUseCases = useCases
        )
    }

    fun calculateStorageSimulation(
        accessFrequencyDays: Int,
        retentionMonths: Int,
        isMultiRegion: Boolean,
        hasLifecycleRule: Boolean
    ): StorageSimulationResult {
        val (recommendedClass, costPerGb, examRule) = when {
            accessFrequencyDays <= 1 -> Triple("Standard Storage", 0.020, "Best for active files accessed daily/weekly. Zero retrieval fees.")
            accessFrequencyDays <= 30 -> Triple("Nearline Storage", 0.010, "Best for data accessed at most once a month. 30-day minimum retention.")
            accessFrequencyDays <= 90 -> Triple("Coldline Storage", 0.004, "Best for quarterly backups or DR archives. 90-day minimum retention.")
            else -> Triple("Archive Storage", 0.0012, "Best for long-term legal archives accessed < 1x a year. 365-day minimum retention.")
        }

        val locationStr = if (isMultiRegion) "Multi-Region (High Availability & Low Latency)" else "Single Region (Cost Effective)"
        val locationMultiplier = if (isMultiRegion) 1.3 else 1.0
        val finalCost = costPerGb * locationMultiplier

        val lifecycleAdvice = if (hasLifecycleRule) {
            "Enabled: Automatically transitions data to colder classes as it ages, optimizing long-term storage costs."
        } else {
            "Recommended: Enable Object Lifecycle Management to auto-transition or delete files older than 30/90/365 days."
        }

        return StorageSimulationResult(
            recommendedClass = recommendedClass,
            recommendedLocation = locationStr,
            estimatedStorageCostPerGb = Math.round(finalCost * 10000.0) / 10000.0,
            lifecycleRecommendation = lifecycleAdvice,
            keyExamRule = examRule
        )
    }

    fun getGcpTerms(): List<GcpTerm> = listOf(
        GcpTerm(
            acronymOrTerm = "Pub/Sub",
            fullName = "Cloud Pub/Sub",
            category = "Messaging & Serverless",
            definition = "Fully managed, real-time asynchronous messaging service that decouples event producers (publishers) from event consumers (subscribers).",
            aceExamTip = "Use when decoupling microservices or buffering high-volume traffic spikes before writing to a database."
        ),
        GcpTerm(
            acronymOrTerm = "Bigtable",
            fullName = "Cloud Bigtable",
            category = "Storage & Databases",
            definition = "Ultra-low latency, wide-column NoSQL database designed for high-throughput streaming writes and time-series analytics.",
            aceExamTip = "Choose for IoT telemetry, financial ticks, or streaming data requiring >10k writes/sec with sub-10ms latency."
        ),
        GcpTerm(
            acronymOrTerm = "Spanner",
            fullName = "Cloud Spanner",
            category = "Storage & Databases",
            definition = "Enterprise globally distributed relational database offering horizontal scaling, ANSI SQL support, and strong multi-region ACID consistency.",
            aceExamTip = "Choose when you need global relational SQL with ACID transactions across multi-region deployments."
        ),
        GcpTerm(
            acronymOrTerm = "Firestore",
            fullName = "Google Cloud Firestore",
            category = "Storage & Databases",
            definition = "Serverless NoSQL document database with live client synchronization, real-time listeners, and offline mobile data caching.",
            aceExamTip = "Ideal for mobile/web apps requiring offline sync and live real-time updates."
        ),
        GcpTerm(
            acronymOrTerm = "BQ",
            fullName = "BigQuery",
            category = "Storage & Databases",
            definition = "Serverless, petabyte-scale cloud data warehouse for fast SQL analytics (OLAP) and business intelligence reporting.",
            aceExamTip = "Choose for analytical queries and BI reports, NOT for transactional row updates (OLTP)."
        ),
        GcpTerm(
            acronymOrTerm = "Cloud SQL",
            fullName = "Cloud SQL for MySQL/PostgreSQL/SQL Server",
            category = "Storage & Databases",
            definition = "Managed relational database service for standard web applications and regional OLTP database workloads up to 64TB.",
            aceExamTip = "Best for traditional relational database applications migrating to Google Cloud."
        ),
        GcpTerm(
            acronymOrTerm = "Cloud Run",
            fullName = "Google Cloud Run",
            category = "Messaging & Serverless",
            definition = "Fully managed serverless execution environment for running stateless Docker containers with auto-scaling down to zero.",
            aceExamTip = "Choose when hosting a custom Docker container statelessly with HTTP traffic and zero infrastructure management."
        ),
        GcpTerm(
            acronymOrTerm = "Cloud Functions",
            fullName = "Google Cloud Functions",
            category = "Messaging & Serverless",
            definition = "Event-driven serverless FaaS (Function-as-a-Service) executed automatically in response to Cloud Storage, Pub/Sub, or HTTP events.",
            aceExamTip = "Use for single-purpose event handlers (e.g. generating image thumbnails upon Cloud Storage upload)."
        ),
        GcpTerm(
            acronymOrTerm = "GKE",
            fullName = "Google Kubernetes Engine",
            category = "Compute & Containers",
            definition = "Managed Kubernetes platform for container deployment, stateful sets, microservice orchestration, and automated cluster scaling.",
            aceExamTip = "Choose GKE over Cloud Run if you need stateful storage volumes, Kubernetes CRDs, or fine-grained pod affinity."
        ),
        GcpTerm(
            acronymOrTerm = "MIG",
            fullName = "Managed Instance Group",
            category = "Compute & Containers",
            definition = "Set of identical Compute Engine VM instances deployed from an Instance Template with autohealing, load balancing, and autoscaling.",
            aceExamTip = "Select MIGs whenever an exam question mentions autohealing or autoscaling Compute Engine VMs."
        ),
        GcpTerm(
            acronymOrTerm = "GCS",
            fullName = "Google Cloud Storage",
            category = "Storage & Databases",
            definition = "Highly durable object storage for unstructured data, featuring Standard, Nearline, Coldline, and Archive access tiers.",
            aceExamTip = "Archive storage tier provides the lowest cost per GB for data accessed less than once per year."
        ),
        GcpTerm(
            acronymOrTerm = "IAM",
            fullName = "Identity and Access Management",
            category = "Networking & Security",
            definition = "Security framework authorizing WHO (Member identity) has WHAT ROLE (Permissions) on WHICH RESOURCE.",
            aceExamTip = "Enforce the Principle of Least Privilege using Predefined Roles instead of Primitive (Owner/Editor/Viewer) roles."
        ),
        GcpTerm(
            acronymOrTerm = "VPC",
            fullName = "Virtual Private Cloud",
            category = "Networking & Security",
            definition = "Global virtual private network providing secure private IP networking for resources within GCP.",
            aceExamTip = "VPCs are global; subnets are regional; VM instances are zonal."
        ),
        GcpTerm(
            acronymOrTerm = "Cloud Armor",
            fullName = "Google Cloud Armor",
            category = "Networking & Security",
            definition = "Web Application Firewall (WAF) and DDoS protection service integrated with Global HTTP(S) Load Balancers.",
            aceExamTip = "Use for IP whitelist/blacklist filtering, Geo-blocking, and OWASP Top 10 web attack mitigation."
        ),
        GcpTerm(
            acronymOrTerm = "Cloud VPN",
            fullName = "Google Cloud VPN",
            category = "Networking & Security",
            definition = "IPsec VPN connecting on-premises data centers to GCP VPCs securely over the public internet (up to 3 Gbps/tunnel).",
            aceExamTip = "Use HA VPN (dual tunnels) for 99.99% availability over public internet at low cost."
        ),
        GcpTerm(
            acronymOrTerm = "Interconnect",
            fullName = "Dedicated Interconnect",
            category = "Networking & Security",
            definition = "Direct physical fiber-optic connection between corporate data centers and Google Edge facility (10/100 Gbps private connection).",
            aceExamTip = "Choose for high-bandwidth (>10 Gbps), low-latency enterprise connections that must NOT traverse the public internet."
        ),
        GcpTerm(
            acronymOrTerm = "CUD",
            fullName = "Committed Use Discount",
            category = "Operations & Billing",
            definition = "Discounted pricing in exchange for committing to a minimum level of compute resource usage for 1 or 3 years.",
            aceExamTip = "Best for steady-state baseline production workloads running continuously."
        ),
        GcpTerm(
            acronymOrTerm = "SUD",
            fullName = "Sustained Use Discount",
            category = "Operations & Billing",
            definition = "Automatic discount applied to Compute Engine VMs running for more than 25% of a billing month.",
            aceExamTip = "Applied automatically by Google Cloud with no configuration or commitment required."
        ),
        GcpTerm(
            acronymOrTerm = "Spot VM",
            fullName = "Spot / Preemptible VM",
            category = "Compute & Containers",
            definition = "Compute Engine VMs priced at up to 90% discount that Google can terminate with a 30-second warning if capacity is needed.",
            aceExamTip = "Ideal for fault-tolerant batch processing, stateless worker queues, and dev/test environments."
        ),
        GcpTerm(
            acronymOrTerm = "Service Account",
            fullName = "IAM Service Account",
            category = "Networking & Security",
            definition = "Special IAM identity used by applications or VMs (not human users) to authenticate and invoke GCP APIs.",
            aceExamTip = "Assign service accounts with narrow predefined roles to VMs rather than embedding API keys in application code."
        ),
        GcpTerm(
            acronymOrTerm = "HPA",
            fullName = "Horizontal Pod Autoscaler",
            category = "Compute & Containers",
            definition = "Kubernetes component automatically adjusting pod replica counts based on observed CPU utilization or custom metrics.",
            aceExamTip = "Used in GKE to automatically handle variable application request traffic."
        ),
        GcpTerm(
            acronymOrTerm = "DLQ",
            fullName = "Dead Letter Queue",
            category = "Messaging & Serverless",
            definition = "Pub/Sub subscription mechanism routing messages that fail processing multiple times to a designated side topic.",
            aceExamTip = "Prevents malformed/poisoned messages from repeatedly breaking consumer processing loops."
        ),
        GcpTerm(
            acronymOrTerm = "CMEK / CSEK",
            fullName = "Customer-Managed / Customer-Supplied Encryption Keys",
            category = "Networking & Security",
            definition = "Customer-Managed Keys (Cloud KMS) vs Customer-Supplied Keys (held on-premises and provided per API call).",
            aceExamTip = "GCP encrypts all data at rest by default. Use CMEK or CSEK when regulatory compliance requires custom key management."
        ),
        GcpTerm(
            acronymOrTerm = "Cloud Operations",
            fullName = "Cloud Logging & Cloud Monitoring",
            category = "Operations & Management",
            definition = "Unified observability suite for metrics, dashboards, alerts, and log routing across Google Cloud workloads.",
            aceExamTip = "Log Router sinks allow exporting logs to Cloud Storage (archival), BigQuery (analytics), or Pub/Sub (streaming)."
        )
    )
}
