# 설치 스크립트

## 환경

ubuntu 24.04 LTS, ARM64

## Java 설치

- java 21 correto
- [설치 가이드 문서](https://docs.aws.amazon.com/corretto/latest/corretto-21-ug/generic-linux-install.html)

### 스크립트

```text
wget -O - https://apt.corretto.aws/corretto.key | sudo gpg --dearmor -o /usr/share/keyrings/corretto-keyring.gpg && \
echo "deb [signed-by=/usr/share/keyrings/corretto-keyring.gpg] https://apt.corretto.aws stable main" | sudo tee /etc/apt/sources.list.d/corretto.list
sudo apt-get update; sudo apt-get install -y java-21-amazon-corretto-jdk
java -version
```

## Docker, Docker Compose 설치

- [설치 가이드 문서](https://docs.docker.com/engine/install/ubuntu/#install-using-the-repository)

### 스크립트

```text
1. Add Docker's official GPG key
sudo apt-get update
sudo apt-get install ca-certificates curl
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

2. Add the repository to Apt sources
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null
sudo apt-get update

3. Install with apt repository
sudo apt-get install docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin

4.
sudo docker --version

5.
sudo groupadd docker

6.
sudo usermod -aG docker $USER

7.
newgrp docker
```

## Cloudwatch Agent 설치

### 스크립트

```text
1. 설치
cd ~
wget https://amazoncloudwatch-agent.s3.amazonaws.com/ubuntu/arm64/latest/amazon-cloudwatch-agent.deb
sudo dpkg -i amazon-cloudwatch-agent.deb
rm amazon-cloudwatch-agent.deb

2. 실행
sudo systemctl enable amazon-cloudwatch-agent.service

3. 구성 파일 로딩
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
  -a fetch-config -m ec2 \
  -c file:구성파일위치 -s

4. 실행 확인
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
  -a status
sudo systemctl status amazon-cloudwatch-agent.service
```
