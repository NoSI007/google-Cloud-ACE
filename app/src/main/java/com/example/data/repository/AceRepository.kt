package com.example.data.repository

import com.example.data.local.AceFlashcardDao
import com.example.data.local.AceFlashcardEntity
import com.example.data.local.BookmarkedTipEntity
import com.example.data.local.CompletedLessonEntity
import com.example.data.local.QuizScoreEntity
import com.example.data.local.UserProgressDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

class AceRepository(
    private val progressDao: UserProgressDao,
    private val flashcardDao: AceFlashcardDao? = null
) {

    val completedLessonIds: Flow<List<String>> = progressDao.getCompletedLessonIds()
    val bookmarkedTips: Flow<List<BookmarkedTipEntity>> = progressDao.getAllBookmarks()
    val quizScores: Flow<List<QuizScoreEntity>> = progressDao.getQuizScores()

    val allRoomFlashcards: Flow<List<AceFlashcardEntity>> = flashcardDao?.getAllFlashcards() ?: emptyFlow()

    suspend fun ensureFlashcardsSeeded() {
        if (flashcardDao != null) {
            val count = flashcardDao.getFlashcardCount()
            if (count == 0) {
                flashcardDao.insertFlashcards(getInitialSeedFlashcards())
            }
        }
    }

    suspend fun updateFlashcardMastery(cardId: String, isMastered: Boolean) {
        flashcardDao?.updateMastery(cardId, isMastered)
    }

    suspend fun resetAllFlashcardProgress() {
        flashcardDao?.resetAllMastery()
    }

    suspend fun insertCustomFlashcard(card: AceFlashcardEntity) {
        flashcardDao?.insertFlashcard(card)
    }

    fun getInitialSeedFlashcards(): List<AceFlashcardEntity> = listOf(
        AceFlashcardEntity(
            id = "fc_gce",
            serviceName = "Google Compute Engine (GCE)",
            serviceCategory = "Compute",
            frontPrompt = "What is Google Compute Engine (GCE)?",
            backDefinition = "Infrastructure as a Service (IaaS) offering customizable Virtual Machines running in Google's data centers.",
            examTip = "Use Spot / Preemptible VMs for fault-tolerant batch workloads to save up to 80-90% cost.",
            keyFeaturesCsv = "Custom Machine Types, Live Migration, Persistent Disks, Managed Instance Groups"
        ),
        AceFlashcardEntity(
            id = "fc_gke",
            serviceName = "Google Kubernetes Engine (GKE)",
            serviceCategory = "Compute",
            frontPrompt = "What is Google Kubernetes Engine (GKE)?",
            backDefinition = "Managed Kubernetes environment for deploying, managing, and scaling containerized applications.",
            examTip = "Autopilot mode manages cluster infrastructure, node provisioning, and autoscaling automatically.",
            keyFeaturesCsv = "Autopilot & Standard Modes, Horizontal Pod Autoscaling, Regional High Availability, Workload Identity"
        ),
        AceFlashcardEntity(
            id = "fc_cloud_run",
            serviceName = "Google Cloud Run",
            serviceCategory = "Compute",
            frontPrompt = "What is Google Cloud Run?",
            backDefinition = "Fully managed serverless platform that runs stateless HTTP containers on demand with scale-to-zero capability.",
            examTip = "Ideal for web apps and microservices packaged as Docker containers without managing Kubernetes clusters.",
            keyFeaturesCsv = "Scales to zero instances, Per-request billing, Built-in HTTPS endpoints, Custom concurrency limits"
        ),
        AceFlashcardEntity(
            id = "fc_cloud_functions",
            serviceName = "Google Cloud Functions",
            serviceCategory = "Compute",
            frontPrompt = "What is Google Cloud Functions?",
            backDefinition = "Serverless event-driven Functions-as-a-Service (FaaS) triggered by GCP events (Pub/Sub, GCS buckets, HTTP).",
            examTip = "Use Cloud Functions to process file uploads in GCS or react instantly to Pub/Sub message streams.",
            keyFeaturesCsv = "Automatic scaling, Event trigger bindings, Node/Python/Go/Java runtimes, Zero server maintenance"
        ),
        AceFlashcardEntity(
            id = "fc_app_engine",
            serviceName = "Google App Engine (GAE)",
            serviceCategory = "Compute",
            frontPrompt = "What is Google App Engine (GAE)?",
            backDefinition = "Platform as a Service (PaaS) for building web applications without managing underlying infrastructure.",
            examTip = "Standard Environment provides instant scaling and free tier; Flexible Environment runs custom Docker containers.",
            keyFeaturesCsv = "Standard vs Flexible environments, Traffic splitting for A/B testing, Automatic SSL certificates"
        ),
        AceFlashcardEntity(
            id = "fc_gcs",
            serviceName = "Google Cloud Storage (GCS)",
            serviceCategory = "Storage & Database",
            frontPrompt = "What is Google Cloud Storage (GCS)?",
            backDefinition = "Scalable, durable object storage service for unstructured data like images, backups, and logs.",
            examTip = "Choose Standard for frequent access, Nearline (30-day min), Coldline (90-day min), or Archive (365-day min).",
            keyFeaturesCsv = "4 Storage Classes, Object Versioning, Bucket Lifecycle Management, Signed URLs"
        ),
        AceFlashcardEntity(
            id = "fc_cloud_sql",
            serviceName = "Cloud SQL",
            serviceCategory = "Storage & Database",
            frontPrompt = "What is Cloud SQL?",
            backDefinition = "Fully managed relational database service for MySQL, PostgreSQL, and SQL Server.",
            examTip = "Enable High Availability (HA) with regional failover replicas for automatic failover within seconds.",
            keyFeaturesCsv = "Managed backups & point-in-time recovery, Read replicas, Automated patching, Regional HA failover"
        ),
        AceFlashcardEntity(
            id = "fc_cloud_spanner",
            serviceName = "Cloud Spanner",
            serviceCategory = "Storage & Database",
            frontPrompt = "What is Cloud Spanner?",
            backDefinition = "Enterprise-grade, fully managed relational database with horizontal scaling and global ACID compliance.",
            examTip = "Use Spanner when you need relational SQL + global scale + 99.999% availability (higher than Cloud SQL).",
            keyFeaturesCsv = "Global ACID transactions, Unlimited scale, Automatic sharding, TrueTime synchronized clocks"
        ),
        AceFlashcardEntity(
            id = "fc_cloud_bigtable",
            serviceName = "Cloud Bigtable",
            serviceCategory = "Storage & Database",
            frontPrompt = "What is Cloud Bigtable?",
            backDefinition = "NoSQL wide-column database service designed for petabyte-scale operational and analytical workloads.",
            examTip = "Ideal for high-throughput time-series, IoT telemetry, and financial data with millisecond latency.",
            keyFeaturesCsv = "Sub-10ms latency, Petabyte scale, HBase API compatible, Single row atomic writes"
        ),
        AceFlashcardEntity(
            id = "fc_firestore",
            serviceName = "Cloud Firestore",
            serviceCategory = "Storage & Database",
            frontPrompt = "What is Google Cloud Firestore?",
            backDefinition = "NoSQL document database built for automatic scaling, rich querying, and real-time synchronization.",
            examTip = "Offers Datastore Mode (backward compatible) and Native Mode (real-time listeners and offline support).",
            keyFeaturesCsv = "Document-oriented NoSQL, Offline mobile support, Real-time sync listeners, Multi-region consistency"
        ),
        AceFlashcardEntity(
            id = "fc_vpc",
            serviceName = "Virtual Private Cloud (VPC)",
            serviceCategory = "Networking",
            frontPrompt = "What is Virtual Private Cloud (VPC)?",
            backDefinition = "Global software-defined network providing isolated private network connectivity for GCP resources.",
            examTip = "VPCs are global resources in GCP, whereas Subnets are regional resources tied to specific IP ranges.",
            keyFeaturesCsv = "Global scope, Regional subnets, Private Google Access, Shared VPC across projects"
        ),
        AceFlashcardEntity(
            id = "fc_load_balancing",
            serviceName = "Cloud Load Balancing",
            serviceCategory = "Networking",
            frontPrompt = "What is Google Cloud Load Balancing?",
            backDefinition = "Fully distributed, software-defined managed load balancer capable of routing millions of requests per second.",
            examTip = "Global External HTTP(S) Load Balancer provides a single global Anycast IP address across all regions.",
            keyFeaturesCsv = "Single Anycast IP, Cross-region failover, Integrated Cloud Armor security, SSL offloading"
        ),
        AceFlashcardEntity(
            id = "fc_iam",
            serviceName = "Cloud IAM",
            serviceCategory = "Security & IAM",
            frontPrompt = "What is Identity & Access Management (IAM) in GCP?",
            backDefinition = "Security framework controlling 'Who' (identity) has 'What access' (role) to 'Which resource'.",
            examTip = "Follow the Principle of Least Privilege. Use Predefined or Custom Roles rather than Primitive (Owner/Editor/Viewer) roles.",
            keyFeaturesCsv = "Primitive, Predefined, and Custom Roles, Service Accounts, Audit Logging, IAM Recommender"
        ),
        AceFlashcardEntity(
            id = "fc_service_accounts",
            serviceName = "Service Accounts",
            serviceCategory = "Security & IAM",
            frontPrompt = "What is a Service Account in GCP?",
            backDefinition = "Special Google account used by applications or VMs (non-human identities) to make authenticated API requests.",
            examTip = "Use Workload Identity for GKE pods instead of exporting long-lived JSON service account keys!",
            keyFeaturesCsv = "Short-lived tokens, IAM role assignment, Workload Identity, Service Account impersonation"
        ),
        AceFlashcardEntity(
            id = "fc_kms",
            serviceName = "Cloud KMS",
            serviceCategory = "Security & IAM",
            frontPrompt = "What is Cloud Key Management Service (KMS)?",
            backDefinition = "Cloud service for generating, rotating, and managing cryptographic keys (CMEK) to encrypt data at rest.",
            examTip = "By default, GCP encrypts all data at rest with Google-managed keys. Use KMS when you need Customer-Managed Encryption Keys (CMEK).",
            keyFeaturesCsv = "Customer-Managed Encryption Keys (CMEK), Automated key rotation, HSM hardware protection, Audit trails"
        ),
        AceFlashcardEntity(
            id = "fc_monitoring",
            serviceName = "Cloud Monitoring (Stackdriver)",
            serviceCategory = "DevOps & Operations",
            frontPrompt = "What is Cloud Monitoring?",
            backDefinition = "Full-stack monitoring service providing metrics, dashboards, health checks, and automated alerting.",
            examTip = "Install the Ops Agent on Compute Engine VMs to collect system-level metrics (memory, disk space) and logs.",
            keyFeaturesCsv = "Metrics & Custom Dashboards, Uptime checks, Alerting policies (Email, Slack), Ops Agent"
        ),
        AceFlashcardEntity(
            id = "fc_logging",
            serviceName = "Cloud Logging",
            serviceCategory = "DevOps & Operations",
            frontPrompt = "What is Cloud Logging?",
            backDefinition = "Centralized log management service for storing, searching, analyzing, and routing application and audit logs.",
            examTip = "Create Log Sinks to export logs to Cloud Storage (archive), BigQuery (analysis), or Pub/Sub (streaming).",
            keyFeaturesCsv = "Log Router & Sinks, Real-time log view, Retention buckets, Audit Logs"
        ),
        AceFlashcardEntity(
            id = "fc_bigquery",
            serviceName = "BigQuery",
            serviceCategory = "Big Data & AI",
            frontPrompt = "What is Google BigQuery?",
            backDefinition = "Serverless, highly scalable enterprise data warehouse with built-in SQL analysis and ML capabilities.",
            examTip = "Partition tables by time/date and Cluster by high-cardinality columns to dramatically cut SQL query costs.",
            keyFeaturesCsv = "Serverless SQL engine, Table Partitioning & Clustering, Federated queries, BigQuery ML"
        ),
        AceFlashcardEntity(
            id = "fc_pubsub",
            serviceName = "Cloud Pub/Sub",
            serviceCategory = "Big Data & AI",
            frontPrompt = "What is Cloud Pub/Sub?",
            backDefinition = "Asynchronous messaging service that decouples event-producing publishers from event-consuming subscribers.",
            examTip = "Guarantees at-least-once message delivery with global availability for event-driven architectures.",
            keyFeaturesCsv = "Push & Pull subscriptions, Dead-letter topics, Seek & Replay, At-least-once delivery"
        ),
        AceFlashcardEntity(
            id = "fc_cloud_build",
            serviceName = "Cloud Build",
            serviceCategory = "DevOps & Operations",
            frontPrompt = "What is Google Cloud Build?",
            backDefinition = "Serverless CI/CD platform that executes build steps in Docker containers on Google Cloud infrastructure.",
            examTip = "Define build steps in a cloudbuild.yaml file and trigger builds automatically from GitHub pushes.",
            keyFeaturesCsv = "Containerized build steps, cloudbuild.yaml workflows, Automated build triggers, Secret Manager integration"
        ),
        AceFlashcardEntity(
            id = "fc_cloud_billing",
            serviceName = "Cloud Billing & Budgets",
            serviceCategory = "Billing & Governance",
            frontPrompt = "How do Cloud Budgets and Billing Alerts function?",
            backDefinition = "Cost-tracking tools that notify stakeholders via Email or Cloud Pub/Sub when spend thresholds (e.g. 50%, 90%, 100%) are reached.",
            examTip = "CRITICAL: Budgets DO NOT stop resources automatically! To shut down VMs or disable billing, connect the Pub/Sub alert to a Cloud Function.",
            keyFeaturesCsv = "Threshold percentage alerts, Pub/Sub programmatic webhooks, BigQuery billing export, Credit card management"
        ),
        AceFlashcardEntity(
            id = "fc_billing_roles",
            serviceName = "Billing IAM Roles",
            serviceCategory = "Billing & Governance",
            frontPrompt = "What is the difference between Billing Account User and Billing Account Admin?",
            backDefinition = "Billing Account User links projects to billing without seeing financial details; Admin manages credit cards and payment instruments.",
            examTip = "Grant 'roles/billing.user' on the billing account + 'roles/resourcemanager.projectCreator' to allow developers to create self-funded projects.",
            keyFeaturesCsv = "roles/billing.admin, roles/billing.user, roles/billing.projectManager, roles/billing.viewer"
        ),
        AceFlashcardEntity(
            id = "fc_service_account_user",
            serviceName = "Service Account User Role",
            serviceCategory = "Security & IAM",
            frontPrompt = "Why is 'roles/iam.serviceAccountUser' required?",
            backDefinition = "Allows a human user or service to attach and execute jobs under the identity of a specific Service Account on a VM or resource.",
            examTip = "Assign this role at the Service Account level (not project-wide) to prevent unauthorized privilege escalation!",
            keyFeaturesCsv = "Identity attachment, Prevents privilege escalation, VM execution identity, Impersonation control"
        ),
        AceFlashcardEntity(
            id = "fc_cud_discounts",
            serviceName = "Committed Use Discounts (CUDs)",
            serviceCategory = "Billing & Governance",
            frontPrompt = "When should you purchase Committed Use Discounts (CUDs)?",
            backDefinition = "Contractual commitments for 1 or 3 years of continuous vCPU, RAM, or database usage in exchange for discounts up to 57–70%.",
            examTip = "Ideal for predictable, steady-state baseline production workloads that run 24/7/365.",
            keyFeaturesCsv = "1-year or 3-year terms, Up to 70% savings, Resource-based or Spend-based, Cross-VM pooling"
        )
    )

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
                    title = "IAM Permissions, Roles & Service Accounts",
                    subtitle = "Managing members, role hierarchy, least privilege, and service account keys.",
                    readingTimeMinutes = 6,
                    contentSections = listOf(
                        LessonSection(
                            heading = "Identity and Access Management (IAM) Fundamentals",
                            bodyParagraphs = listOf(
                                "IAM controls WHO (member identity) has WHAT PERMISSION (role) on WHICH RESOURCE. Permissions follow the format: 'service.resource.verb' (e.g., 'compute.instances.start', 'storage.buckets.get'). You cannot assign atomic permissions directly to a user; instead, you assign Roles that bundle multiple permissions.",
                                "Role Types in GCP:\n• Primitive Roles: Viewer, Editor, Owner (Broad, legacy roles across all services; avoid in production!).\n• Predefined Roles: Granular, service-specific roles curated and maintained by Google (e.g., 'roles/compute.instanceAdmin.v1', 'roles/storage.objectViewer').\n• Custom Roles: User-defined collections of specific permissions created when predefined roles are too permissive."
                            ),
                            tableRows = listOf(
                                "Primitive Roles" to "Owner, Editor, Viewer • High blast radius • Not recommended for production",
                                "Predefined Roles" to "Curated by GCP • Follow least privilege (e.g. roles/spanner.databaseAdmin)",
                                "Custom Roles" to "Tailored list of granular permissions • Cannot be applied at Folder or Org level unless created there"
                            )
                        ),
                        LessonSection(
                            heading = "IAM Policy Inheritance & Policy Additive Rule",
                            bodyParagraphs = listOf(
                                "Permissions inherit strictly downwards from Organization -> Folders -> Projects -> Resources.",
                                "Crucial Rule: IAM policies are ADDITIVE. If a user is granted 'Editor' at the Folder level, you CANNOT remove or revoke that permission at a specific Project inside that folder. Always grant permissions at the lowest necessary node in the hierarchy!"
                            ),
                            codeOrConceptSnippet = "Org (No broad roles) ──> Folder ──> Project [Assign roles/compute.admin here] ──> VM Instance"
                        ),
                        LessonSection(
                            heading = "Service Accounts & Machine Identities",
                            bodyParagraphs = listOf(
                                "A Service Account is a non-human identity used by applications (VMs, Cloud Run, GKE) to authenticate to Google APIs.",
                                "Key Roles for Service Accounts:\n• Service Account User (roles/iam.serviceAccountUser): Allows a human developer to attach/run an existing service account on a VM.\n• Service Account Token Creator: Generates short-lived OAuth tokens for service account impersonation.\n• Workload Identity Federation: Best practice for authenticating external CI/CD (GitHub Actions, AWS) without generating static JSON keys."
                            ),
                            codeOrConceptSnippet = "# Granting access via gcloud\ngcloud projects add-iam-policy-binding my-project-id \\\n    --member=\"user:engineer@company.com\" \\\n    --role=\"roles/compute.instanceAdmin.v1\""
                        )
                    ),
                    keyTakeaways = listOf(
                        "Always follow Principle of Least Privilege: use Predefined Roles instead of Primitive Owner/Editor.",
                        "IAM is additive down the hierarchy: child resources inherit all parent permissions and cannot revoke them.",
                        "Service Account User ('roles/iam.serviceAccountUser') allows users to attach identities to VMs."
                    ),
                    aceExamTips = listOf(
                        "ACE Exam Rule: If a developer needs to deploy a VM that uses a Service Account, they need both 'Compute Admin' on the project AND 'Service Account User' on the target Service Account!"
                    )
                ),
                AceLesson(
                    id = "les_1_4",
                    title = "Cloud Billing, Budgets & Cost Optimization",
                    subtitle = "Billing hierarchy, IAM billing roles, budgets, alerts, and discount models.",
                    readingTimeMinutes = 6,
                    contentSections = listOf(
                        LessonSection(
                            heading = "GCP Billing Architecture & Accounts",
                            bodyParagraphs = listOf(
                                "A Cloud Billing Account is attached to one or more Projects to pay for all resource consumption. A Project must be linked to a valid Billing Account to enable paid APIs and create billable resources.",
                                "Key Billing IAM Roles:\n• Billing Account Creator (roles/billing.creator): Grants ability to create new Billing Accounts (typically at Org level).\n• Billing Account Administrator (roles/billing.admin): Full control over payment methods and billing setup.\n• Billing Account User (roles/billing.user): Allows linking projects to the Billing Account.\n• Project Billing Manager (roles/billing.projectManager): Links/unlinks a specific project to/from a Billing Account.\n• Billing Account Viewer (roles/billing.viewer): Read-only access to view spend reports and invoices."
                            ),
                            tableRows = listOf(
                                "Billing Account Administrator" to "Manage credit cards, payment profiles, and budgets across the billing account.",
                                "Billing Account User" to "Link projects to the billing account without seeing financial payment details.",
                                "Project Billing Manager" to "Link or unlink an individual project to an approved billing account.",
                                "Billing Account Viewer" to "Auditors and finance staff viewing invoice breakdowns."
                            )
                        ),
                        LessonSection(
                            heading = "Budgets, Alerts & Automated Spend Caps",
                            bodyParagraphs = listOf(
                                "Budgets allow tracking costs against a target amount. Alerts notify team members when spending reaches percentages of the budget (e.g., 50%, 90%, 100%, or forecasted 100%).",
                                "CRITICAL ACE EXAM POINT: Cloud Budgets and Billing Alerts DO NOT STOP OR SHUT DOWN RESOURCES BY DEFAULT! An alert only sends an email or Pub/Sub notification. To enforce an automated hard spend cap, you must send budget alerts to a Cloud Pub/Sub topic and trigger a Cloud Function to disable billing or stop VMs programmatically."
                            ),
                            codeOrConceptSnippet = "Budget Alert Reaches 100% ──> Pub/Sub Notification ──> Cloud Function ──> Disables Billing API"
                        ),
                        LessonSection(
                            heading = "Billing BigQuery Export & Cost Optimization Models",
                            bodyParagraphs = listOf(
                                "1. BigQuery Billing Export: Streams standard usage, detailed resource tags/labels, and pricing data directly into BigQuery tables for SQL cost analytics and Looker dashboards.",
                                "2. Cost Optimization Discounts:\n• Sustained Use Discounts (SUDs): Automatic discounts (up to 30%) applied when Compute Engine VMs run for more than 25% of a billing month without any upfront commitment.\n• Committed Use Discounts (CUDs): Contractual commitments (1-year or 3-year) for predictable resource usage offering up to 57–70% discount.\n• Spot VMs: Up to 91% discount for interruptible, fault-tolerant batch workloads."
                            ),
                            tableRows = listOf(
                                "Sustained Use (SUD)" to "100% Automatic • Applies to N1, N2 VMs running continuously (>25% of month).",
                                "Committed Use (CUD)" to "1 or 3 Year Contract • High discount (up to 70%) • For predictable baseline workloads.",
                                "Spot VMs" to "60-91% Off • Fault-tolerant batch processing • Can be preempted with 30s notice."
                            )
                        )
                    ),
                    keyTakeaways = listOf(
                        "Billing Budgets DO NOT stop resources automatically; you must use Pub/Sub + Cloud Functions for hard limits.",
                        "BigQuery Billing Export is the recommended destination for automated billing SQL analysis.",
                        "To allow a developer to create projects linked to billing, grant them 'roles/resourcemanager.projectCreator' and 'roles/billing.user'."
                    ),
                    aceExamTips = listOf(
                        "ACE Exam Scenario: If an exam question asks how to ensure a team can create new projects and link them to corporate billing without giving them full billing admin rights, assign 'Billing Account User' on the Billing Account!"
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
        // Module 1: Cloud Core & Environment Setup
        QuizQuestion(
            id = 1,
            topicCategory = "1. Cloud Core & Environment",
            questionText = "Your company wants to grant a developer team access to create and manage VMs in a specific project without allowing them to modify billing configuration or delete the project. Where should you apply the IAM role?",
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
            topicCategory = "1. Cloud Core & Environment",
            questionText = "According to the GCP Shared Responsibility Model, which security task is the customer responsible for on Google Compute Engine (IaaS)?",
            options = listOf(
                "Physical security of Google data centers",
                "Hypervisor maintenance and server hardware replacement",
                "Guest operating system security updates and application patches",
                "Decommissioning failing physical hard drives"
            ),
            correctOptionIndex = 2,
            explanation = "On IaaS (Compute Engine), Google manages the underlying physical infrastructure and hypervisor, but the customer is responsible for guest OS patching, application software, and IAM configurations."
        ),
        QuizQuestion(
            id = 3,
            topicCategory = "1. Cloud Core & Environment",
            questionText = "You need to export all Cloud Billing export data automatically for long-term SQL querying and visualization in Looker Studio. What is the recommended destination?",
            options = listOf(
                "Google Cloud Storage Standard Bucket via gsutil rsync",
                "BigQuery dataset linked directly in Cloud Billing Export",
                "Cloud SQL for MySQL instance",
                "Firestore database in Datastore mode"
            ),
            correctOptionIndex = 1,
            explanation = "Google Cloud Billing has native integration with BigQuery. Enabling BigQuery export automatically streams daily and detailed cost data into BigQuery tables for SQL analytics."
        ),

        // Module 2: Virtual Machines & Compute Engine
        QuizQuestion(
            id = 4,
            topicCategory = "2. Compute Engine & VMs",
            questionText = "You need to run a non-critical daily batch data transformation job that takes 4 hours to complete. What is the most cost-effective Compute Engine configuration?",
            options = listOf(
                "Standard N2 VM instance with automated snapshot backups",
                "Spot VM (Preemptible) instance with checkpointing to save intermediate progress",
                "Memory-Optimized M2 instance running 24/7",
                "Compute-Optimized C2 instance with Local SSD"
            ),
            correctOptionIndex = 1,
            explanation = "Spot VMs offer discounts up to 90% compared to standard instances. Because the job is non-critical batch processing that can handle interruptions with checkpointing, Spot VMs are the most cost-effective choice."
        ),
        QuizQuestion(
            id = 5,
            topicCategory = "2. Compute Engine & VMs",
            questionText = "An application running on a Compute Engine VM requires extremely high read/write IOPS with sub-millisecond latency for temporary cache data. What disk type should you attach?",
            options = listOf(
                "Standard Persistent Disk (pd-standard)",
                "Local SSD",
                "Regional Persistent Disk (pd-ssd)",
                "Cloud Storage Bucket mounted via gcsfuse"
            ),
            correctOptionIndex = 1,
            explanation = "Local SSDs are physically attached to the server hosting the VM, delivering sub-millisecond latency and maximum IOPS for temporary cache or scratchpad data."
        ),
        QuizQuestion(
            id = 6,
            topicCategory = "2. Compute Engine & VMs",
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

        // Module 3: Storage & Database Solutions
        QuizQuestion(
            id = 7,
            topicCategory = "3. Storage & Databases",
            questionText = "Your company must store regulatory audit logs for 5 years. The logs will almost never be accessed after creation. What Cloud Storage class and feature should you use?",
            options = listOf(
                "Standard Storage with manual monthly object migration",
                "Archive Storage with an Object Lifecycle Management policy",
                "Nearline Storage with Object Versioning disabled",
                "Coldline Storage stored on a Local SSD"
            ),
            correctOptionIndex = 1,
            explanation = "Archive Storage provides the lowest storage cost for data accessed less than once a year (with a 365-day minimum storage duration). Object Lifecycle Management automates retention and transitions."
        ),
        QuizQuestion(
            id = 8,
            topicCategory = "3. Storage & Databases",
            questionText = "You need a managed relational database that supports standard PostgreSQL syntax and automatic failover for a regional e-commerce website. Which service should you choose?",
            options = listOf(
                "BigQuery",
                "Cloud Bigtable",
                "Cloud SQL for PostgreSQL",
                "Firestore"
            ),
            correctOptionIndex = 2,
            explanation = "Cloud SQL is GCP's fully managed relational database service supporting PostgreSQL, MySQL, and SQL Server for standard regional OLTP workloads with High Availability (HA) automatic failover."
        ),
        QuizQuestion(
            id = 9,
            topicCategory = "3. Storage & Databases",
            questionText = "A global financial institution needs a database for international transactions requiring global ACID compliance, horizontal scale, strong consistency across multi-region deployments, and standard ANSI SQL. Which service should they choose?",
            options = listOf(
                "BigQuery",
                "Cloud Spanner",
                "Cloud Bigtable",
                "Cloud SQL"
            ),
            correctOptionIndex = 1,
            explanation = "Cloud Spanner is GCP's globally distributed relational database offering unlimited horizontal scaling, strong multi-region ACID consistency, and standard SQL support."
        ),
        QuizQuestion(
            id = 10,
            topicCategory = "3. Storage & Databases",
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

        // Module 4: Containers & Serverless
        QuizQuestion(
            id = 11,
            topicCategory = "4. Containers & Serverless",
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
            id = 12,
            topicCategory = "4. Containers & Serverless",
            questionText = "You need to run single-purpose Python code triggered automatically whenever a new JPEG image is uploaded to a Cloud Storage bucket. What is the most lightweight, cost-efficient serverless compute service?",
            options = listOf(
                "Google Cloud Functions (2nd gen)",
                "Google Compute Engine VM with cron job polling",
                "Google Kubernetes Engine Autopilot cluster",
                "App Engine Standard Environment"
            ),
            correctOptionIndex = 0,
            explanation = "Cloud Functions is an event-driven serverless FaaS service. It can bind directly to Cloud Storage object finalize events with zero idle server cost."
        ),
        QuizQuestion(
            id = 13,
            topicCategory = "4. Containers & Serverless",
            questionText = "What is the key operational difference between GKE Autopilot mode and GKE Standard mode?",
            options = listOf(
                "Autopilot does not support Docker containers",
                "Autopilot manages cluster infrastructure, node provisioning, and autoscaling automatically, charging per pod resource usage",
                "Standard mode does not allow persistent disks",
                "Autopilot requires manual Linux OS kernel updates on worker nodes"
            ),
            correctOptionIndex = 1,
            explanation = "GKE Autopilot is a hands-off managed Kubernetes mode where Google provisions and optimizes the nodes, node pools, and security hardening, and you pay only for pod resource requests."
        ),

        // Module 5: Security & Access Management
        QuizQuestion(
            id = 14,
            topicCategory = "5. Security & Access Management",
            questionText = "An application running on a Compute Engine VM needs to read objects from a Cloud Storage bucket. What is the Google Cloud recommended best practice for granting access?",
            options = listOf(
                "Generate an API key and embed it into the application source code",
                "Create a Service Account with the 'Storage Object Viewer' role and attach it to the VM",
                "Grant the user's personal Gmail account Owner permissions on the project",
                "Make the Cloud Storage bucket public with allUsers permissions"
            ),
            correctOptionIndex = 1,
            explanation = "Service Accounts allow automated applications and VMs to authenticate securely without storing static passwords or credentials. Assigning the narrow predefined role 'Storage Object Viewer' follows least privilege."
        ),
        QuizQuestion(
            id = 15,
            topicCategory = "5. Security & Access Management",
            questionText = "A financial services client requires that cryptographic encryption keys for BigQuery tables be managed in Google Cloud KMS and automatically rotated every 90 days. What encryption model is this?",
            options = listOf(
                "Default Google-Managed Encryption (GMK)",
                "Customer-Managed Encryption Keys (CMEK)",
                "Customer-Supplied Encryption Keys (CSEK)",
                "Client-Side Hardware Security Module (HSM) bypass"
            ),
            correctOptionIndex = 1,
            explanation = "Customer-Managed Encryption Keys (CMEK) use Cloud KMS inside Google Cloud to control key rotation schedules, permissions, and audit logs."
        ),
        QuizQuestion(
            id = 16,
            topicCategory = "5. Security & Access Management",
            questionText = "You need to protect your public-facing HTTP(S) Load Balancer against Layer 7 DDoS attacks and SQL injection attempts. Which Google Cloud service provides this Web Application Firewall (WAF) capability?",
            options = listOf(
                "Cloud Armor",
                "VPC Firewall Rules",
                "Cloud NAT",
                "Identity-Aware Proxy (IAP)"
            ),
            correctOptionIndex = 0,
            explanation = "Google Cloud Armor provides enterprise DDoS defense and WAF rules (OWASP Top 10 mitigation, rate limiting, and IP whitelisting) directly integrated at Google's global load balancing edge."
        ),

        // Module 6: Cloud Networking & Hybrid
        QuizQuestion(
            id = 17,
            topicCategory = "6. Cloud Networking & Hybrid",
            questionText = "A healthcare enterprise requires a private, dedicated physical connection between their corporate data center and Google Cloud with 10 Gbps throughput and a 99.99% availability SLA without routing data over the public internet. What should they implement?",
            options = listOf(
                "Cloud VPN with multiple HA tunnels",
                "Dedicated Interconnect",
                "VPC Peering over Cloud Router",
                "Partner Interconnect with 50 Mbps bandwidth"
            ),
            correctOptionIndex = 1,
            explanation = "Dedicated Interconnect provides a direct physical fiber connection between an enterprise data center and Google's network edge with 10 Gbps / 100 Gbps pipes and 99.99% availability without using the public internet."
        ),
        QuizQuestion(
            id = 18,
            topicCategory = "6. Cloud Networking & Hybrid",
            questionText = "Which statement accurately describes the scope of VPC networks, subnets, and Compute Engine VM instances in Google Cloud?",
            options = listOf(
                "VPCs are zonal, subnets are regional, VMs are global",
                "VPCs are global, subnets are regional, and VM instances are zonal",
                "VPCs are regional, subnets are zonal, and VM instances are global",
                "VPCs, subnets, and VM instances are all zonal"
            ),
            correctOptionIndex = 1,
            explanation = "In Google Cloud, a VPC Network is global (spanning all regions worldwide), Subnets are regional (bound to a specific geographical region), and VM instances are zonal (bound to a single zone within a region)."
        ),
        QuizQuestion(
            id = 19,
            topicCategory = "6. Cloud Networking & Hybrid",
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
            id = 20,
            topicCategory = "6. Cloud Networking & Hybrid",
            questionText = "Two separate organizations want to connect their respective VPC networks to communicate privately using internal IP addresses with high bandwidth and no gateway bottleneck. What networking solution should they configure?",
            options = listOf(
                "Cloud NAT Gateway",
                "VPC Network Peering",
                "External HTTP(S) Load Balancer",
                "Direct Physical Fiber Splice"
            ),
            correctOptionIndex = 1,
            explanation = "VPC Network Peering enables direct, private RFC 1918 communication between distinct VPC networks within Google's SDN with zero bandwidth bottleneck and no external gateway."
        ),

        // Billing & IAM Permissions Deep Dive Questions
        QuizQuestion(
            id = 21,
            topicCategory = "1. Cloud Core & Environment",
            questionText = "You need to allow a team lead to create new Google Cloud projects and link them to your company's corporate Billing Account, without giving them permission to change payment methods or modify other projects. Which two IAM roles should you grant?",
            options = listOf(
                "Project Owner and Billing Account Administrator",
                "roles/resourcemanager.projectCreator at Organization level and roles/billing.user on the Billing Account",
                "roles/resourcemanager.folderAdmin and roles/billing.viewer",
                "roles/iam.serviceAccountAdmin and roles/billing.projectManager"
            ),
            correctOptionIndex = 1,
            explanation = "Project Creator (roles/resourcemanager.projectCreator) allows creating new projects. Billing Account User (roles/billing.user) allows associating existing/new projects to the billing account without exposing sensitive financial or credit card management."
        ),
        QuizQuestion(
            id = 22,
            topicCategory = "1. Cloud Core & Environment",
            questionText = "Your management wants to ensure a non-production development project never incurs more than $500 in monthly charges. You configure a Cloud Budget with a $500 threshold. What happens when spending reaches $500?",
            options = listOf(
                "Google Cloud automatically deletes all VM instances and Cloud SQL databases",
                "Billing is automatically disabled and all resources are frozen",
                "Alert notifications (email / Pub/Sub) are sent, but resources continue running and billing continues unless programmatic actions are configured",
                "Compute Engine network bandwidth is throttled to zero"
            ),
            correctOptionIndex = 2,
            explanation = "Cloud Budgets and alerts DO NOT automatically stop or cap resources by default. To enforce an automated hard cap, you must send budget alerts to a Cloud Pub/Sub topic and trigger a Cloud Function to disable the billing API programmatically."
        ),
        QuizQuestion(
            id = 23,
            topicCategory = "5. Security & Access Management",
            questionText = "A developer is assigned the 'Compute Instance Admin' role on a project and tries to launch a VM configured to run as a custom Service Account 'app-sa@project.iam.gserviceaccount.com'. The deployment fails with a permissions error. What additional role does the developer need?",
            options = listOf(
                "Service Account Key Admin on the Project",
                "Service Account User (roles/iam.serviceAccountUser) on the target Service Account",
                "Organization Administrator",
                "Security Reviewer"
            ),
            correctOptionIndex = 1,
            explanation = "To attach or run a Service Account identity on a resource (like a Compute Engine VM), the user initiating the deployment must have the 'Service Account User' role (roles/iam.serviceAccountUser) granted on that Service Account."
        ),
        QuizQuestion(
            id = 24,
            topicCategory = "5. Security & Access Management",
            questionText = "A user is granted the 'roles/viewer' role at the Organization level and 'roles/editor' at a specific Folder level. Which statement correctly describes IAM policy inheritance for a project inside that folder?",
            options = listOf(
                "The user only has Viewer access because Org policies override Folder policies",
                "The user has Editor access because IAM policies are unioned/additive down the resource hierarchy",
                "The user has no access because permissions conflict",
                "The user must be explicitly added to the Project IAM policy to gain any access"
            ),
            correctOptionIndex = 1,
            explanation = "In Google Cloud, IAM policies are strictly additive/unioned. Permissions inherited from parent nodes (Org, Folder) cannot be revoked at child levels, and the effective access is the union of all granted roles."
        ),
        QuizQuestion(
            id = 25,
            topicCategory = "1. Cloud Core & Environment",
            questionText = "Your company runs 20 N2 Virtual Machines 24/7 with predictable steady-state CPU and memory usage for an enterprise ERP system expected to operate for the next 3 years. What is the most cost-effective discount strategy?",
            options = listOf(
                "Rely solely on Sustained Use Discounts (SUDs)",
                "Purchase a 3-Year Committed Use Discount (CUD) for the baseline vCPU and RAM requirements",
                "Convert all instances to Spot VMs",
                "Stop and start the instances every 4 hours"
            ),
            correctOptionIndex = 1,
            explanation = "For predictable, continuous 24/7 baseline workloads over 1 to 3 years, Committed Use Discounts (CUDs) provide the highest savings (up to 57%-70%), significantly higher than default Sustained Use Discounts."
        ),
        QuizQuestion(
            id = 26,
            topicCategory = "5. Security & Access Management",
            questionText = "Your team uses GitHub Actions CI/CD pipelines to deploy container images to Google Artifact Registry. What is the Google Cloud recommended best practice for authenticating GitHub Actions without storing long-lived service account JSON keys?",
            options = listOf(
                "Generate a Service Account JSON key and commit it to the Git repository",
                "Configure Workload Identity Federation with OpenID Connect (OIDC)",
                "Store the root Google account password in GitHub Secrets",
                "Use an unauthenticated Cloud Storage bucket"
            ),
            correctOptionIndex = 1,
            explanation = "Workload Identity Federation allows external workloads (like GitHub Actions, AWS, Azure) to impersonate a Google Cloud service account using short-lived tokens via OIDC, completely eliminating risky long-lived service account JSON keys."
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

    fun getCliCommands(): List<GcloudCliCommand> = listOf(
        GcloudCliCommand(
            id = "cmd_config_set_project",
            command = "gcloud config set project [PROJECT_ID]",
            description = "Sets the active default project in the current gcloud CLI configuration context.",
            category = "Config & Auth",
            syntaxBreakdown = "gcloud config set [PROPERTY] [VALUE]",
            commonFlags = listOf(
                "--project=[ID]" to "Overrides the active project for a single command execution",
                "--configuration=[NAME]" to "Applies setting to a specific named configuration profile"
            ),
            aceExamTip = "Always verify the active project using 'gcloud config get-value project' before creating billable resources.",
            exampleOutput = "Updated property [core/project]."
        ),
        GcloudCliCommand(
            id = "cmd_config_configurations_create",
            command = "gcloud config configurations create [CONFIG_NAME]",
            description = "Creates a new named configuration profile to easily switch between multiple accounts, projects, or regions.",
            category = "Config & Auth",
            syntaxBreakdown = "gcloud config configurations create [NAME] && gcloud config configurations activate [NAME]",
            commonFlags = listOf(
                "activate" to "Switches current terminal session to the specified configuration profile",
                "list" to "Displays all existing configuration profiles and identifies the active one"
            ),
            aceExamTip = "Use multiple configurations when working across distinct customer projects (e.g. dev, staging, prod).",
            exampleOutput = "Created [staging-config]. Activated [staging-config]."
        ),
        GcloudCliCommand(
            id = "cmd_auth_login",
            command = "gcloud auth login",
            description = "Authenticates user credentials with Google Cloud via web browser OAuth2 flow.",
            category = "Config & Auth",
            syntaxBreakdown = "gcloud auth login [ACCOUNT_EMAIL]",
            commonFlags = listOf(
                "--no-launch-browser" to "Outputs authentication URL for headless terminal or remote SSH session",
                "application-default login" to "Acquires user credentials for local SDK/client library code testing"
            ),
            aceExamTip = "Use 'gcloud auth application-default login' when developing local apps calling Google Cloud APIs.",
            exampleOutput = "You are now logged in as [developer@example.com]."
        ),
        GcloudCliCommand(
            id = "cmd_compute_instances_create",
            command = "gcloud compute instances create [VM_NAME] --zone=[ZONE] --machine-type=[TYPE] --image-family=[FAMILY] --image-project=[PROJECT] --service-account=[SA_EMAIL] --scopes=cloud-platform",
            description = "Deploys a new Compute Engine Virtual Machine instance with specified hardware, OS image, and service account.",
            category = "Compute Engine",
            syntaxBreakdown = "gcloud compute instances create [NAME] [FLAGS...]",
            commonFlags = listOf(
                "--zone=[ZONE]" to "Specifies the deployment zone (e.g. us-central1-a). VMs are strictly zonal",
                "--machine-type=[TYPE]" to "Configures vCPU/RAM profile (e.g. e2-medium, n2-standard-4)",
                "--service-account=[EMAIL]" to "Attaches IAM identity to the VM for secure API authentication",
                "--scopes=cloud-platform" to "Grants API access scope (best practice is full scope + narrow IAM role on SA)",
                "--tags=[TAG1,TAG2]" to "Attaches network target tags for VPC firewall rule filtering",
                "--preemptible / --provisioning-model=SPOT" to "Deploys as Spot VM with up to 90% discount"
            ),
            aceExamTip = "Never hardcode API keys on VMs. Always attach a custom Service Account with 'cloud-platform' scope and assign least-privilege IAM roles.",
            exampleOutput = "NAME: web-vm  ZONE: us-central1-a  MACHINE_TYPE: e2-medium  INTERNAL_IP: 10.128.0.2  STATUS: RUNNING"
        ),
        GcloudCliCommand(
            id = "cmd_compute_set_machine_type",
            command = "gcloud compute instances set-machine-type [VM_NAME] --machine-type=[NEW_TYPE] --zone=[ZONE]",
            description = "Resizes the vCPU and memory of an existing Compute Engine VM instance.",
            category = "Compute Engine",
            syntaxBreakdown = "gcloud compute instances stop [NAME] && gcloud compute instances set-machine-type [NAME] --machine-type=[TYPE] && gcloud compute instances start [NAME]",
            commonFlags = listOf(
                "--machine-type=[TYPE]" to "Target machine type (e.g. n2-standard-8, e2-standard-4)",
                "--zone=[ZONE]" to "Target zone where the instance resides"
            ),
            aceExamTip = "ACE Exam Rule: You MUST STOP the VM instance before you can change its machine type. Running VMs cannot be resized dynamically.",
            exampleOutput = "Updated [https://www.googleapis.com/compute/v1/projects/my-proj/zones/us-central1-a/instances/web-vm]."
        ),
        GcloudCliCommand(
            id = "cmd_compute_instance_template",
            command = "gcloud compute instance-templates create [TEMPLATE_NAME] --machine-type=e2-standard-2 --image-family=debian-11 --image-project=debian-cloud --tags=http-server",
            description = "Creates a reusable template defining VM configuration for Managed Instance Groups (MIGs).",
            category = "Compute Engine",
            syntaxBreakdown = "gcloud compute instance-templates create [NAME] [FLAGS...]",
            commonFlags = listOf(
                "--source-instance=[VM]" to "Creates a template cloned directly from an existing VM",
                "--metadata-from-file=startup-script=[PATH]" to "Embeds automated bootstrap startup script"
            ),
            aceExamTip = "Instance templates are immutable global resources. To update a MIG's configuration, create a new template and update the MIG.",
            exampleOutput = "Created [https://www.googleapis.com/compute/v1/projects/my-proj/global/instanceTemplates/web-template-v1]."
        ),
        GcloudCliCommand(
            id = "cmd_compute_mig_create",
            command = "gcloud compute instance-groups managed create [MIG_NAME] --template=[TEMPLATE] --size=3 --zone=[ZONE]",
            description = "Creates a zonal or regional Managed Instance Group (MIG) for autohealing and load balancing.",
            category = "Compute Engine",
            syntaxBreakdown = "gcloud compute instance-groups managed create [NAME] --template=[TEMPLATE] --size=[N] [--zone=[Z] | --region=[R]]",
            commonFlags = listOf(
                "--region=[REGION]" to "Creates a Regional MIG spanning multiple zones for high availability",
                "--health-check=[HC]" to "Attaches autohealing health check to replace unhealthy instances",
                "--initial-delay=[SEC]" to "Grace period before health checks start monitoring newly booted VMs"
            ),
            aceExamTip = "Regional MIGs offer higher availability than zonal MIGs because they distribute VMs across 3 zones automatically.",
            exampleOutput = "Created [https://www.googleapis.com/compute/v1/projects/my-proj/zones/us-central1-a/instanceGroupManagers/web-mig]."
        ),
        GcloudCliCommand(
            id = "cmd_compute_mig_autoscale",
            command = "gcloud compute instance-groups managed set-autoscaling [MIG_NAME] --max-num-replicas=10 --min-num-replicas=2 --target-cpu-utilization=0.75 --cool-down-period=60s",
            description = "Configures automated dynamic scaling based on CPU utilization, Cloud Monitoring metrics, or Load Balancer capacity.",
            category = "Compute Engine",
            syntaxBreakdown = "gcloud compute instance-groups managed set-autoscaling [NAME] [FLAGS...]",
            commonFlags = listOf(
                "--target-cpu-utilization=[0.0-1.0]" to "Fractional CPU target (e.g. 0.75 for 75%)",
                "--target-load-balancing-utilization=[0.0-1.0]" to "Scales based on backend HTTP load balancer capacity",
                "--custom-metric-utilization" to "Scales based on custom Cloud Monitoring metric (e.g. Pub/Sub queue depth)"
            ),
            aceExamTip = "Autoscaling requires an instance template. When traffic drops, MIG removes instances down to --min-num-replicas.",
            exampleOutput = "Autoscaler is active on managed instance group [web-mig]."
        ),
        GcloudCliCommand(
            id = "cmd_compute_ssh",
            command = "gcloud compute ssh [VM_NAME] --zone=[ZONE] --tunnel-through-iap",
            description = "Establishes secure SSH shell session into a Compute Engine VM, optionally tunneling through Identity-Aware Proxy (IAP) without public IP.",
            category = "Compute Engine",
            syntaxBreakdown = "gcloud compute ssh [INSTANCE_NAME] --zone=[ZONE] [FLAGS...]",
            commonFlags = listOf(
                "--tunnel-through-iap" to "Connects to VM without external public IP via TCP forwarding on port 22",
                "--command='[SHELL_CMD]'" to "Runs a single remote command without opening an interactive shell"
            ),
            aceExamTip = "For secure zero-trust bastionless administration, use IAP Desktop or '--tunnel-through-iap' so VMs don't need external public IPs.",
            exampleOutput = "Connected to web-vm (10.128.0.2)..."
        ),
        GcloudCliCommand(
            id = "cmd_iam_sa_create",
            command = "gcloud iam service-accounts create [SA_NAME] --display-name='[DISPLAY_NAME]'",
            description = "Creates a new Service Account identity in the current project.",
            category = "IAM & Security",
            syntaxBreakdown = "gcloud iam service-accounts create [NAME] --display-name=[TEXT]",
            commonFlags = listOf(
                "--description=[TEXT]" to "Provides purpose and architectural context for the service account",
                "--project=[ID]" to "Project in which to create the service account"
            ),
            aceExamTip = "The resulting email format is [SA_NAME]@[PROJECT_ID].iam.gserviceaccount.com.",
            exampleOutput = "Created service account [backend-worker]."
        ),
        GcloudCliCommand(
            id = "cmd_iam_add_binding",
            command = "gcloud projects add-iam-policy-binding [PROJECT_ID] --member='serviceAccount:[SA_EMAIL]' --role='roles/storage.objectViewer'",
            description = "Grants an IAM role to a user, group, or service account on a Google Cloud project.",
            category = "IAM & Security",
            syntaxBreakdown = "gcloud projects add-iam-policy-binding [PROJECT_ID] --member=[MEMBER] --role=[ROLE]",
            commonFlags = listOf(
                "--member='user:[EMAIL]'" to "Grants role to a Google Account user",
                "--member='group:[EMAIL]'" to "Grants role to a Google Group (best practice for team access)",
                "--member='serviceAccount:[EMAIL]'" to "Grants role to a service account",
                "--condition='[EXPR]'" to "Attaches IAM Condition (e.g. expire after date, IP range, resource tag)"
            ),
            aceExamTip = "Always grant specific Predefined Roles (e.g. roles/storage.objectViewer) rather than Primitive Roles (Owner/Editor/Viewer).",
            exampleOutput = "Updated IAM policy for project [my-project-123]."
        ),
        GcloudCliCommand(
            id = "cmd_iam_sa_user_role",
            command = "gcloud iam service-accounts add-iam-policy-binding [SA_EMAIL] --member='user:[DEVELOPER_EMAIL]' --role='roles/iam.serviceAccountUser'",
            description = "Authorizes a developer to attach and run workloads under a specific Service Account identity.",
            category = "IAM & Security",
            syntaxBreakdown = "gcloud iam service-accounts add-iam-policy-binding [SA_EMAIL] --member=[USER] --role=roles/iam.serviceAccountUser",
            commonFlags = listOf(
                "--role='roles/iam.serviceAccountUser'" to "Required to assign the service account to VMs, Cloud Run, or GKE pods"
            ),
            aceExamTip = "Grant 'Service Account User' on the specific service account resource itself, not at project level, to prevent privilege escalation.",
            exampleOutput = "Updated IAM policy for service account [backend-worker@my-proj.iam.gserviceaccount.com]."
        ),
        GcloudCliCommand(
            id = "cmd_gcs_mb",
            command = "gcloud storage buckets create gs://[BUCKET_NAME] --location=[LOCATION] --default-storage-class=[CLASS] --uniform-bucket-level-access",
            description = "Creates a new Google Cloud Storage bucket with specified storage tier and uniform access controls.",
            category = "Cloud Storage",
            syntaxBreakdown = "gcloud storage buckets create gs://[NAME] [FLAGS...]",
            commonFlags = listOf(
                "--location=[REGION/MULTI-REGION]" to "Bucket location (e.g. us-central1, us, eu, asia)",
                "--default-storage-class=[STANDARD/NEARLINE/COLDLINE/ARCHIVE]" to "Default tier for uploaded objects",
                "--uniform-bucket-level-access" to "Enforces uniform IAM permissions across all objects (disables legacy ACLs)"
            ),
            aceExamTip = "Bucket names are globally unique across all Google Cloud customers. Uniform Bucket-Level Access is Google's recommended security best practice.",
            exampleOutput = "Creating gs://my-company-prod-backups/... OK"
        ),
        GcloudCliCommand(
            id = "cmd_gcs_rsync",
            command = "gcloud storage rsync -r -d [LOCAL_DIR] gs://[BUCKET_NAME]",
            description = "Synchronizes directories and bucket contents by copying new/modified files and deleting removed files.",
            category = "Cloud Storage",
            syntaxBreakdown = "gcloud storage rsync [FLAGS...] [SRC] [DEST]",
            commonFlags = listOf(
                "-r / --recursive" to "Recursively synchronizes all subdirectories",
                "-d / --delete-unmatched-destination-objects" to "Deletes files in destination that do not exist in source",
                "-x / --exclude=[REGEX]" to "Excludes files matching regular expression"
            ),
            aceExamTip = "Use 'rsync' for incremental backups and 'storage cp -m' for parallel bulk multi-threaded uploads.",
            exampleOutput = "Copying file://app.log to gs://my-bucket/app.log [100%]"
        ),
        GcloudCliCommand(
            id = "cmd_gcs_lifecycle",
            command = "gcloud storage buckets update gs://[BUCKET_NAME] --lifecycle-file=[LIFECYCLE_CONFIG.json]",
            description = "Applies automated lifecycle management rules to transition objects to colder tiers or delete expired data.",
            category = "Cloud Storage",
            syntaxBreakdown = "gcloud storage buckets update gs://[BUCKET] --lifecycle-file=[JSON]",
            commonFlags = listOf(
                "--lifecycle-file=[FILE]" to "JSON file specifying conditions (Age, CreatedBefore, MatchesStorageClass) and actions (SetStorageClass, Delete)"
            ),
            aceExamTip = "Lifecycle rules take up to 24 hours to take effect and run daily asynchronously.",
            exampleOutput = "Updating gs://my-bucket/... OK"
        ),
        GcloudCliCommand(
            id = "cmd_gke_cluster_create",
            command = "gcloud container clusters create [CLUSTER_NAME] --num-nodes=3 --zone=[ZONE] --enable-autoscaling --min-nodes=1 --max-nodes=6 --enable-ip-alias",
            description = "Deploys a managed Google Kubernetes Engine (GKE) cluster with VPC-native routing and node autoscaling.",
            category = "GKE & Containers",
            syntaxBreakdown = "gcloud container clusters create [NAME] [FLAGS...]",
            commonFlags = listOf(
                "--enable-ip-alias" to "Enables VPC-native cluster routing (pods get native VPC IP addresses)",
                "--enable-autoscaling" to "Enables Cluster Autoscaler on the default node pool",
                "--enable-autorepair" to "Automatically drains and rebuilds unhealthy Kubernetes nodes",
                "--enable-autoupgrade" to "Keeps master and worker node versions aligned with GKE release channels",
                "--workload-pool=[PROJECT_ID].svc.id.goog" to "Enables Workload Identity for pod-to-GCP-API IAM mapping"
            ),
            aceExamTip = "VPC-native clusters (--enable-ip-alias) are mandatory for private clusters, Cloud Interconnect, and alias IP routing.",
            exampleOutput = "NAME: prod-cluster  LOCATION: us-central1-a  MASTER_VERSION: 1.28  NUM_NODES: 3  STATUS: RUNNING"
        ),
        GcloudCliCommand(
            id = "cmd_gke_get_credentials",
            command = "gcloud container clusters get-credentials [CLUSTER_NAME] --zone=[ZONE]",
            description = "Configures kubectl CLI context and client certificates in ~/.kube/config to interact with the GKE cluster.",
            category = "GKE & Containers",
            syntaxBreakdown = "gcloud container clusters get-credentials [NAME] [--zone=[Z] | --region=[R]]",
            commonFlags = listOf(
                "--region=[REGION]" to "Used for Regional GKE clusters",
                "--zone=[ZONE]" to "Used for Zonal GKE clusters"
            ),
            aceExamTip = "Run this command first whenever switching terminal workstations before executing any kubectl commands.",
            exampleOutput = "Fetching cluster endpoint and auth data.\nkubeconfig entry generated for prod-cluster."
        ),
        GcloudCliCommand(
            id = "cmd_cloud_run_deploy",
            command = "gcloud run deploy [SERVICE_NAME] --image=gcr.io/[PROJECT]/[IMAGE] --platform=managed --region=[REGION] --allow-unauthenticated",
            description = "Deploys a containerized stateless web service to fully managed serverless Cloud Run with automatic scaling down to zero.",
            category = "Cloud Run & Functions",
            syntaxBreakdown = "gcloud run deploy [NAME] --image=[URI] [FLAGS...]",
            commonFlags = listOf(
                "--allow-unauthenticated" to "Permits public internet traffic (grants roles/run.invoker to allUsers)",
                "--no-allow-unauthenticated" to "Requires IAM authentication token to invoke service",
                "--min-instances=[N]" to "Keeps warm container instances to prevent cold start latency",
                "--max-instances=[N]" to "Caps maximum concurrent container instances for cost safety",
                "--set-env-vars=[KEY=VAL]" to "Injects runtime environment variables"
            ),
            aceExamTip = "Cloud Run scales to zero when there is no traffic, incurring zero cost when idle.",
            exampleOutput = "Service [orders-api] revision [orders-api-00001-abc] has been deployed and is serving 100 percent of traffic.\nService URL: https://orders-api-abc.a.run.app"
        ),
        GcloudCliCommand(
            id = "cmd_cloud_functions_deploy",
            command = "gcloud functions deploy [FUNCTION_NAME] --runtime=python311 --trigger-topic=[TOPIC_NAME] --region=[REGION] --entry-point=[FUNC]",
            description = "Deploys an event-driven serverless function responding to Pub/Sub, Cloud Storage, or HTTP webhook triggers.",
            category = "Cloud Run & Functions",
            syntaxBreakdown = "gcloud functions deploy [NAME] --runtime=[RUNTIME] [TRIGGER_FLAG] [FLAGS...]",
            commonFlags = listOf(
                "--trigger-http" to "Exposes function as an HTTP REST endpoint",
                "--trigger-topic=[TOPIC]" to "Executes function upon each message published to a Pub/Sub topic",
                "--trigger-bucket=[BUCKET]" to "Executes function upon object creation/modification in Cloud Storage",
                "--memory=[MB/GB]" to "Allocates RAM (default 256MB, up to 32GB)"
            ),
            aceExamTip = "Event-driven Cloud Functions are idempotent by design; implement deduplication logic when handling Pub/Sub messages.",
            exampleOutput = "State: ACTIVE  URL: https://us-central1-my-proj.cloudfunctions.net/resize-img"
        ),
        GcloudCliCommand(
            id = "cmd_vpc_subnet_create",
            command = "gcloud compute networks subnets create [SUBNET_NAME] --network=[VPC_NAME] --region=[REGION] --range=10.10.0.0/24 --enable-private-ip-google-access",
            description = "Creates a regional subnet within a custom VPC network with Private Google Access enabled.",
            category = "Networking & VPC",
            syntaxBreakdown = "gcloud compute networks subnets create [NAME] --network=[VPC] --region=[R] --range=[CIDR] [FLAGS...]",
            commonFlags = listOf(
                "--enable-private-ip-google-access" to "Allows VMs without public IPs to access Google APIs (GCS, BigQuery) privately",
                "--enable-flow-logs" to "Captures sample packet telemetry for security auditing and network troubleshooting"
            ),
            aceExamTip = "Private Google Access is enabled per subnet and allows internal IP-only VMs to query storage.googleapis.com and other Google APIs without Cloud NAT or external IPs.",
            exampleOutput = "Created [https://www.googleapis.com/compute/v1/projects/my-proj/regions/us-central1/subnetworks/app-subnet]."
        ),
        GcloudCliCommand(
            id = "cmd_vpc_firewall_create",
            command = "gcloud compute firewall-rules create [RULE_NAME] --network=[VPC] --allow=tcp:80,tcp:443 --target-tags=web-server --source-ranges=0.0.0.0/0",
            description = "Creates a stateful firewall rule controlling ingress or egress traffic to specific target instances.",
            category = "Networking & VPC",
            syntaxBreakdown = "gcloud compute firewall-rules create [NAME] --network=[VPC] [FLAGS...]",
            commonFlags = listOf(
                "--allow=[PROTOCOL:PORT]" to "Specifies allowed traffic (e.g. tcp:80, tcp:22, icmp, all)",
                "--target-tags=[TAGS]" to "Applies rule only to VM instances carrying matching network tags",
                "--target-service-accounts=[SA]" to "Applies rule securely based on VM service account identity",
                "--source-ranges=[CIDR]" to "Restricts traffic source IP range (e.g. 192.168.1.0/24, 35.235.240.0/20 for IAP)",
                "--priority=[0-65535]" to "Rule evaluation precedence (lower integer = higher priority, default 1000)"
            ),
            aceExamTip = "Firewall rules are stateful: if ingress is allowed, response egress traffic is automatically permitted regardless of egress rules.",
            exampleOutput = "Creating firewall-rules... Created [allow-http-web]."
        ),
        GcloudCliCommand(
            id = "cmd_logging_sink_create",
            command = "gcloud logging sinks create [SINK_NAME] storage.googleapis.com/[BUCKET_NAME] --log-filter='severity>=ERROR'",
            description = "Creates a Log Router sink to export filtered audit and operational logs to Cloud Storage, BigQuery, or Pub/Sub.",
            category = "Billing & Budgets",
            syntaxBreakdown = "gcloud logging sinks create [NAME] [DESTINATION] --log-filter=[FILTER]",
            commonFlags = listOf(
                "storage.googleapis.com/[BUCKET]" to "Exports logs to Cloud Storage for long-term compliance archive",
                "bigquery.googleapis.com/projects/[P]/datasets/[D]" to "Exports logs to BigQuery for SQL analytics",
                "pubsub.googleapis.com/projects/[P]/topics/[T]" to "Streams logs to Pub/Sub for real-time SIEM ingestion"
            ),
            aceExamTip = "Log Sinks create a designated Service Account that MUST be granted permissions (e.g. Storage Object Creator) on the destination.",
            exampleOutput = "Created [error-sink]. Please remember to grant [serviceAccount:p123-sink@gcp-sa-logging.iam.gserviceaccount.com] write permissions to the destination."
        ),
        GcloudCliCommand(
            id = "cmd_billing_budget_create",
            command = "gcloud billing budgets create --billing-account=[ACCOUNT_ID] --display-name='Monthly Cap' --budget-amount=1000USD --threshold-rule=percent=0.5,basis=current-spend --threshold-rule=percent=0.9,basis=current-spend --threshold-rule=percent=1.0,basis=forecasted-spend",
            description = "Creates a Cloud Billing budget with multi-tier alert thresholds for spend monitoring.",
            category = "Billing & Budgets",
            syntaxBreakdown = "gcloud billing budgets create --billing-account=[ID] [FLAGS...]",
            commonFlags = listOf(
                "--threshold-rule=percent=N,basis=[current-spend/forecasted-spend]" to "Triggers alert at specified percentage of budget",
                "--notifications-rule-pubsub-topic=[TOPIC]" to "Emits programmatic JSON budget events to Pub/Sub for automated cost remediation"
            ),
            aceExamTip = "Cloud Budgets do NOT shut down resources automatically. To enforce a hard spend cap, connect the budget Pub/Sub alert to a Cloud Function that disables the billing API.",
            exampleOutput = "Created budget [1234-5678-90AB]."
        )
    )

    fun getCloudBestPractices(): List<CloudBestPractice> = listOf(
        CloudBestPractice(
            id = "bp_least_privilege",
            title = "Enforce Principle of Least Privilege with Predefined Roles",
            category = "Security & IAM",
            rule = "Never assign Primitive Roles (Owner, Editor, Viewer) to human users or service accounts in production environments.",
            rationale = "Primitive roles grant sweeping project-wide permissions (e.g. Editor can delete storage buckets and VM disks). Predefined roles (e.g. Storage Object Viewer, Compute Network Admin) limit blast radius.",
            actionableGuideline = "Grant narrow Predefined Roles at the lowest possible resource hierarchy level (Resource > Project > Folder > Organization). Use IAM Conditions to enforce expiration and IP restrictions.",
            antiPattern = "Assigning 'roles/editor' to a backend developer so they 'have access to everything they need'.",
            aceExamPriority = "Critical (High Yield)"
        ),
        CloudBestPractice(
            id = "bp_service_account_keys",
            title = "Eliminate Downloaded Service Account Key Files",
            category = "Security & IAM",
            rule = "Avoid generating and downloading JSON private keys for Service Accounts. Use Workload Identity Federation or attached Service Accounts instead.",
            rationale = "Downloaded JSON key files do not expire by default and are a leading cause of credential leaks in public repositories.",
            actionableGuideline = "For GCP workloads (VMs, Cloud Run, GKE), attach the Service Account directly. For AWS/Azure/GitHub Actions, configure Workload Identity Federation for short-lived token exchange.",
            antiPattern = "Embedding 'sa-key.json' in a Docker image or checking it into git repository.",
            aceExamPriority = "Critical (High Yield)"
        ),
        CloudBestPractice(
            id = "bp_custom_vpc_subnets",
            title = "Deploy Custom Mode VPC Networks in Production",
            category = "Networking Architecture",
            rule = "Do not use the 'Default' auto-mode VPC network for production infrastructure. Always design custom VPC networks with planned CIDR blocks.",
            rationale = "Auto-mode VPC creates subnets in every Google Cloud region using overlapping 10.128.0.0/9 CIDR blocks, preventing VPC Network Peering and hybrid interconnect routing due to IP collision.",
            actionableGuideline = "Create Custom Mode VPC networks and allocate non-overlapping RFC 1918 CIDR ranges only in the regions where you operate. Enable Private Google Access on all subnets.",
            antiPattern = "Deploying production database clusters into the Default VPC with pre-created 0.0.0.0/0 open firewall rules.",
            aceExamPriority = "Critical (High Yield)"
        ),
        CloudBestPractice(
            id = "bp_regional_migs",
            title = "Distribute Compute Workloads Across Regional MIGs",
            category = "High Availability & Resilience",
            rule = "Use Regional Managed Instance Groups (MIGs) rather than Zonal MIGs for production services.",
            rationale = "Regional MIGs automatically distribute VM instances across 3 distinct availability zones in a region, maintaining uptime even if an entire data center zone experiences an outage.",
            actionableGuideline = "Configure a Regional MIG with an Instance Template, autohealing HTTP health checks, and a Target CPU Autoscaling policy. Pair with a Global External HTTP(S) Load Balancer.",
            antiPattern = "Running all production web servers in a single zone (e.g. us-central1-a) without autohealing.",
            aceExamPriority = "Critical (High Yield)"
        ),
        CloudBestPractice(
            id = "bp_storage_lifecycle_rules",
            title = "Automate Data Tiering with Object Lifecycle Management",
            category = "Cost Optimization",
            rule = "Configure Cloud Storage Object Lifecycle Management to transition aging objects from Standard to Nearline (30d), Coldline (90d), and Archive (365d).",
            rationale = "Archive storage ($0.0012/GB/mo) is over 90% cheaper than Standard storage ($0.020/GB/mo). Automated lifecycle rules reduce storage bills without manual maintenance.",
            actionableGuideline = "Define a lifecycle JSON policy with 'Age: 30' to Nearline, 'Age: 90' to Coldline, and 'Age: 365' to Archive. Enable Object Versioning with 'NumNewerVersions: 3' for accidental delete protection.",
            antiPattern = "Storing 5-year-old audit logs and database dumps in Standard multi-region storage buckets indefinitely.",
            aceExamPriority = "Critical (High Yield)"
        ),
        CloudBestPractice(
            id = "bp_committed_use_discounts",
            title = "Cover Steady-State Baseline Compute with 1- or 3-Year CUDs",
            category = "Cost Optimization",
            rule = "Analyze baseline 24/7 vCPU and RAM requirements and purchase Resource-Based or Flexible Committed Use Discounts (CUDs).",
            rationale = "Committed Use Discounts offer up to 57% savings on Compute Engine and up to 70% for memory-optimized workloads with zero architectural changes.",
            actionableGuideline = "Use Cloud Billing Reports and Cost Recommender to identify steady-state baseline VM capacity that has run continuously for >6 months. Commit only to the baseline, using Spot VMs or on-demand for spikes.",
            antiPattern = "Paying full on-demand hourly rates for 50 enterprise database VMs that run 24 hours a day, 365 days a year.",
            aceExamPriority = "Recommended"
        ),
        CloudBestPractice(
            id = "bp_gke_workload_identity",
            title = "Authenticate GKE Pods via Workload Identity",
            category = "Security & IAM",
            rule = "Use GKE Workload Identity to bind Kubernetes ServiceAccounts (KSA) directly to Google IAM Service Accounts (GSA).",
            rationale = "Workload Identity allows fine-grained per-pod IAM permissions without granting broad node-level permissions or mounting static credential secrets into containers.",
            actionableGuideline = "Enable Workload Identity on the GKE cluster: '--workload-pool=[PROJECT_ID].svc.id.goog'. Annotate the KSA with the GSA email and grant 'roles/iam.workloadIdentityUser'.",
            antiPattern = "Granting the GKE Node Pool Service Account 'roles/editor' or 'roles/storage.admin' so that all pods inherit full administrative access.",
            aceExamPriority = "Critical (High Yield)"
        ),
        CloudBestPractice(
            id = "bp_iap_zero_trust_bastion",
            title = "Enforce Zero-Trust Remote Access with Identity-Aware Proxy (IAP)",
            category = "Networking Architecture",
            rule = "Remove public external IP addresses from backend VMs and use Identity-Aware Proxy (IAP) TCP forwarding for SSH (port 22) and RDP (port 3389).",
            rationale = "Eliminating public IPs prevents internet port scanning and brute-force attacks. IAP authenticates users against Google Cloud IAM before opening a secure tunneled proxy connection.",
            actionableGuideline = "Create a firewall rule allowing ingress from IP range 35.235.240.0/20 on ports 22 and 3389. Connect using 'gcloud compute ssh [VM] --tunnel-through-iap' or IAP Desktop.",
            antiPattern = "Assigning public IP addresses to internal database servers and opening port 22/3389 to 0.0.0.0/0.",
            aceExamPriority = "Critical (High Yield)"
        ),
        CloudBestPractice(
            id = "bp_cloud_sql_ha",
            title = "Enable High Availability (Regional HA) on Production Cloud SQL",
            category = "High Availability & Resilience",
            rule = "Always configure Cloud SQL instances in Production with Regional High Availability (HA) and Automated Daily Backups.",
            rationale = "Regional HA creates a synchronous standby replica in a different zone. If the primary zone fails, Cloud SQL performs automatic failover in under 60 seconds with zero data loss.",
            actionableGuideline = "Enable High Availability (HA), Point-in-Time Recovery (PITR) with binary logging, and automated backups during low-traffic maintenance windows. Use read replicas for read-heavy reporting.",
            antiPattern = "Running a single-zone Cloud SQL instance for production e-commerce without automated backups or failover standby.",
            aceExamPriority = "Critical (High Yield)"
        ),
        CloudBestPractice(
            id = "bp_centralized_logging_sinks",
            title = "Export Audit Logs to BigQuery and Cloud Storage via Sinks",
            category = "DevOps & Operations",
            rule = "Configure Cloud Logging Log Router sinks to export Admin Activity and System Event audit logs to BigQuery for security analytics and Cloud Storage for legal archives.",
            rationale = "Cloud Logging default retention is 30 days. Compliance standards (HIPAA, PCI-DSS, SOC2) require multi-year log retention and real-time security anomaly querying.",
            actionableGuideline = "Create an aggregated log sink at the Organization or Folder level routing to a dedicated security project's BigQuery dataset and Cloud Storage bucket.",
            antiPattern = "Relying on default Cloud Logging 30-day retention and losing incident investigation logs during security audits.",
            aceExamPriority = "Architecture Baseline"
        )
    )
}

