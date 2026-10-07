terraform {
  required_version = ">= 1.6.0"

  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }

  backend "s3" {
    bucket       = "flm-terraform-state"
    key          = "dev/terraform.tfstate"
    region       = "us-east-1"
    encrypt      = true
    use_lockfile = true
  }
}

module "infra" {
  source = "../../"

  aws_region                = "us-east-1"
  environment               = "dev"
  project_name              = "flm-cloud-native-platform"
  vpc_cidr                  = "10.0.0.0/16"
  public_subnet_cidrs       = ["10.0.1.0/24", "10.0.2.0/24"]
  private_subnet_cidrs      = ["10.0.11.0/24", "10.0.12.0/24"]
  eks_version               = "1.36"
  node_instance_types       = ["t3.medium"]
  node_desired_size         = 2
  node_min_size             = 1
  node_max_size             = 4
  enable_ecr_image_scanning = true

  additional_tags = {
    CostCenter = "engineering"
    Owner      = "platform-team"
  }
}