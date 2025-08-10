# RuoYi-Cloud-Plus-MT

<div align="center">

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
![JDK](https://img.shields.io/badge/JDK-17+-green.svg)

</div>

## 项目简介

**RuoYi-Cloud-Plus-MT** 是一款专门用于替换 [RuoYi-Cloud-Plus](https://github.com/dromara/RuoYi-Cloud-Plus) 项目中所有 `ruoyi` 字段及 `org.dromara` 字段的工具软件。

该工具旨在帮助企业或个人开发者快速进行二次开发，避免手动修改带来的繁琐和错误风险。

## 功能特性

- 🔧 自动化替换 `ruoyi` 相关字段
- 🔄 批量处理 `org.dromara` 包名结构
- 🎯 专为二次开发优化
- ⚡ 提高项目定制效率

## 环境要求

| 工具 | 版本 |
|------|------|
| JDK  | 17+  |

## 使用方法

1. 确保已安装 JDK 17 或更高版本
2. 下载最新的可执行 jar 包
3. 执行以下命令进行项目替换：
```
java -jar ruoyi-cloud-plus-MT-1.0.0-jar-with-dependencies.jar 
--group-id com.example 
--project-name my-example-project 
--app-name MyExampleProject 
--dest-dir \dest\path 
--zip-path zip\path\RuoYi-Cloud-Plus.zip 
--thread-num 24
```
### 参数说明

| 参数名 | 说明 | 示例 |
|--------|------|------|
| `--group-id` | Maven Group ID | `com.example` |
| `--project-name` | 项目名称 | `my-example-project` |
| `--app-name` | 应用名称 | `MyExampleProject` |
| `--dest-dir` | 输出目录路径 | `\dest\path` |
| `--zip-path` | 源码包路径 | `zip\path\RuoYi-Cloud-Plus.zip` |
| `--thread-num` | 处理线程数 | `24` |

## 适用场景

- 企业级项目定制开发
- 个人学习研究
- 基于 RuoYi-Cloud-Plus 的衍生项目开发

## 注意事项

- 使用前请备份原项目
- 建议在测试环境中先进行验证
- 如遇到问题，请提交 issue 进行反馈
- 本作品尚未支持 Linux 系统
- **推荐使用cmd**

---
<p align="center">
  如果您觉得这个项目对您有帮助，请给个 Star ⭐
</p>