-- 创建用户表
CREATE TABLE user (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 用户ID
                      username VARCHAR(255) NOT NULL, -- 用户名
                      bookmark BOOLEAN -- 书签
    -- 其他用户基本信息字段可以在这里扩展
);

-- 创建电话号码表
CREATE TABLE phone_number (
                              id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 电话号码ID
                              user_id BIGINT NOT NULL, -- 用户ID
                              type VARCHAR(50), -- 电话类型 (如 mobile, home, work)
                              number VARCHAR(50), -- 电话号码
                              FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE -- 外键关联用户表
);

-- 创建电子邮件地址表
CREATE TABLE email_address (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 电子邮件地址ID
                               user_id BIGINT NOT NULL, -- 用户ID
                               type VARCHAR(50), -- 邮件类型 (如 personal, work)
                               email VARCHAR(255), -- 电子邮件地址
                               FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE -- 外键关联用户表
);

-- 创建社交媒体账户表
CREATE TABLE social_media_handle (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 社交媒体账户ID
                                     user_id BIGINT NOT NULL, -- 用户ID
                                     platform VARCHAR(50), -- 平台名称 (如 Twitter, Facebook)
                                     handle VARCHAR(255), -- 用户名/账户
                                     FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE -- 外键关联用户表
);

-- 创建物理地址表
CREATE TABLE physical_address (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY, -- 地址ID
                                  user_id BIGINT NOT NULL, -- 用户ID
                                  type VARCHAR(50), -- 地址类型 (如 home, work)
                                  address TEXT, -- 详细地址
                                  FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE -- 外键关联用户表
);

-- 插入用户数据
INSERT INTO user (username, bookmark) VALUES
                                          ('Alice', TRUE),
                                          ('Bob', FALSE),
                                          ('Charlie', TRUE),
                                          ('Diana', FALSE),
                                          ('Eve', TRUE);

-- 插入电话号码数据
INSERT INTO phone_number (user_id, type, number) VALUES
                                                     (1, 'mobile', '123-456-7890'),
                                                     (1, 'home', '987-654-3210'),
                                                     (2, 'work', '555-123-4567'),
                                                     (3, 'mobile', '234-567-8901'),
                                                     (4, 'home', '678-901-2345'),
                                                     (5, 'work', '789-012-3456');

-- 插入电子邮件地址数据
INSERT INTO email_address (user_id, type, email) VALUES
                                                     (1, 'personal', 'alice@example.com'),
                                                     (2, 'work', 'bob@company.com'),
                                                     (3, 'personal', 'charlie@gmail.com'),
                                                     (4, 'work', 'diana@business.com'),
                                                     (5, 'personal', 'eve@yahoo.com');

-- 插入社交媒体账户数据
INSERT INTO social_media_handle (user_id, platform, handle) VALUES
                                                                (1, 'Twitter', '@alice123'),
                                                                (1, 'Facebook', 'alice.fb'),
                                                                (2, 'Instagram', '@bob_pics'),
                                                                (3, 'Twitter', '@charlie_tweets'),
                                                                (4, 'LinkedIn', 'diana-linkedin'),
                                                                (5, 'Facebook', 'eve.fb');

-- 插入物理地址数据
INSERT INTO physical_address (user_id, type, address) VALUES
                                                          (1, 'home', '123 Maple Street, Springfield'),
                                                          (2, 'work', '456 Oak Avenue, Metropolis'),
                                                          (3, 'home', '789 Pine Road, Gotham'),
                                                          (4, 'work', '321 Birch Lane, Star City'),
                                                          (5, 'home', '654 Cedar Street, Central City');