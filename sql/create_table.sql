-- auto-generated definition
create table user
(
    id         bigint auto_increment comment 'id'
        primary key,
    username   varchar(256)                           null comment '用户名',
    account    varchar(256)                           null comment '账号',
    avatarUrl  varchar(1024)                          null comment '用户头像',
    gender     tinyint                                null comment '性别',
    password   varchar(512)                           not null comment '密码',
    userRole   varchar(256) default 'user'            not null comment '角色 user/admin',
    createTime datetime     default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime datetime     default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint      default 0                 not null comment '是否删除'
)
    comment '用户表' collate = utf8mb4_general_ci;

create index idx_account
    on user (account);

create table chart
(
    id          bigint auto_increment comment 'id'
        primary key,
    goal        text                               null comment '分析目标',
    chartName   varchar(128)                       null comment '图表名称',
    chartData   text                               null comment '图表数据',
    chartType   varchar(128)                       null comment '图表类型',
    AiGenChart  text                               null comment 'AI生成图表数据',
    AiGenResult text                               null comment '生成的分析结论',
    userId      bigint                             null comment '(创建图表的)用户ID',
    createTime  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
    -- 任务状态字段(排队中wait、执行中running、已完成succeed、失败failed)
    status       varchar(128) not null default 'wait' comment 'wait,running,succeed,failed',
-- 任务执行信息字段
    execMessage  text   null comment '执行信息',

    isDelete    tinyint  default 0                 not null comment '是否删除'
)
    comment '图表表';