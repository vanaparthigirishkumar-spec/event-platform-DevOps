locals {
  name_prefix = "${var.project_name}-${var.environment}"

  common_tags = merge({
    Project     = var.project_name
    Environment = var.environment
    ManagedBy   = "terraform"
    Repository  = "flm-cloud-native-platform"
  }, var.additional_tags)

  vpc_tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-vpc"
  })

  public_subnet_tags = merge(local.common_tags, {
    "kubernetes.io/role/elb" = "1"
  })

  private_subnet_tags = merge(local.common_tags, {
    "kubernetes.io/role/internal-elb" = "1"
  })

  eks_tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-eks"
  })

  node_group_tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-node-group"
  })

  ecr_tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-ecr"
  })

  iam_tags = merge(local.common_tags, {
    Name = "${local.name_prefix}-iam"
  })

  availability_zones = slice(data.aws_availability_zones.available.names, 0, 2)
}