export DOCKER_SERVER=public.ecr.aws/r8x8n0y3

# Login to AWS ECR
aws ecr-public get-login-password --region us-east-1 | docker login --username AWS --password-stdin "${DOCKER_SERVER}"

# Tag the existing images so that it will be accepted by AWS ECR
docker tag springboot-note:0.0.1 "${DOCKER_SERVER}"/main/springboot-note:0.0.1
docker tag springboot-note-categorizer:0.0.1 "${DOCKER_SERVER}"/main/springboot-note-categorizer:0.0.1

# Push the previously tagged images to AWS ECR
docker push "${DOCKER_SERVER}"/main/springboot-note:0.0.1
docker push "${DOCKER_SERVER}"/main/springboot-note-categorizer:0.0.1
