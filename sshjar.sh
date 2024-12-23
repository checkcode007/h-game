#!/bin/bash

# 配置部分
REMOTE_SERVER="8.137.63.141"          # 远程服务器的 IP 地址
REMOTE_USER="root"                    # 远程服务器的用户名
REMOTE_PASSWORD="84448113Zcj"         # 远程服务器的密码
REMOTE_PATH="/opt/project/game"       # 远程文件存放路径
LOCAL_FILE="core/build/libs/core-1.0.jar"    # 本地构建的 JAR 文件路径

# 1. 删除远程服务器上的旧文件
echo "Deleting old files on remote server..."
sshpass -p "$REMOTE_PASSWORD" ssh "$REMOTE_USER@$REMOTE_SERVER" "rm -f $REMOTE_PATH/core-1.0.jar"
sshpass -p "$REMOTE_PASSWORD" ssh "$REMOTE_USER@$REMOTE_SERVER" "rm -rf $REMOTE_PATH/logs"

# 2. 上传新文件到远程服务器
echo "Uploading new JAR file..."
sshpass -p "$REMOTE_PASSWORD" scp "$LOCAL_FILE" "$REMOTE_USER@$REMOTE_SERVER:$REMOTE_PATH/"

# 3. 设置文件权限
echo "Setting permissions for the JAR file..."
sshpass -p "$REMOTE_PASSWORD" ssh "$REMOTE_USER@$REMOTE_SERVER" "chmod +x $REMOTE_PATH/core-1.0.jar"

# 4. 重启远程服务
echo "Restarting remote application..."
sshpass -p "$REMOTE_PASSWORD" ssh "$REMOTE_USER@$REMOTE_SERVER" "nohup sh $REMOTE_PATH/restart.sh &"

# 输出完成信息
echo "Deployment completed successfully!"
