CREATE TABLE Course
(
    id               bigint(20) NOT NULL AUTO_INCREMENT,
    createdAt        datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    name             varchar(100) NOT NULL,
    code             varchar(10)  NOT NULL,
    description      text,
    status           enum('ACTIVE', 'INACTIVE') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci DEFAULT 'ACTIVE',
    inactivationDate datetime,
    instructorId     bigint(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT UC_Code UNIQUE (code),
    CONSTRAINT FK_Course_Instructor FOREIGN KEY (instructorId) REFERENCES User (id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC;