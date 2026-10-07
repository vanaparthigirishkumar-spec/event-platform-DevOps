module "vpc" {
  source = "./modules/vpc"

  vpc_cidr             = var.vpc_cidr
  public_subnet_cidrs  = var.public_subnet_cidrs
  private_subnet_cidrs = var.private_subnet_cidrs
  availability_zones   = local.availability_zones
  tags                 = local.vpc_tags
  public_subnet_tags   = local.public_subnet_tags
  private_subnet_tags  = local.private_subnet_tags
  name_prefix          = local.name_prefix
}

module "iam" {
  source = "./modules/iam"

  name_prefix = local.name_prefix
  tags        = local.iam_tags
  vpc_id      = module.vpc.vpc_id
}

module "ecr" {
  source = "./modules/ecr"

  name_prefix           = local.name_prefix
  tags                  = local.ecr_tags
  enable_image_scanning = var.enable_ecr_image_scanning
  repositories          = ["backend", "frontend"]
}

module "eks" {
  source = "./modules/eks"

  name_prefix         = local.name_prefix
  tags                = local.eks_tags
  vpc_id              = module.vpc.vpc_id
  private_subnet_ids  = module.vpc.private_subnet_ids
  eks_version         = var.eks_version
  cluster_role_arn    = module.iam.eks_cluster_role_arn
  node_group_name     = "${local.name_prefix}-nodes"
  node_role_arn       = module.iam.eks_node_role_arn
  node_instance_types = var.node_instance_types
  node_desired_size   = var.node_desired_size
  node_min_size       = var.node_min_size
  node_max_size       = var.node_max_size
}