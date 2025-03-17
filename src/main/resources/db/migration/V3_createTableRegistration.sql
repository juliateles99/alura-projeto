CREATE TABLE Registration
(
    id               bigint(20) NOT NULL AUTO_INCREMENT,
    registrationDate datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    userId           bigint(20) NOT NULL,
    courseId         bigint(20) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT FK_Registration_User FOREIGN KEY (userId) REFERENCES User (id),
    CONSTRAINT FK_Registration_Course FOREIGN KEY (courseId) REFERENCES Course (id),
    CONSTRAINT UC_User_Course UNIQUE (userId, courseId)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci ROW_FORMAT=DYNAMIC; 