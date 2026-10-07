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
    key          = "prod/terraform.tfstate"
    region       = "us-east-1"
    encrypt      = true
    use_lockfile = true
  }
}

module "infra" {
  source = "../../"

  aws_region                = "us-east-1"
  environment               = "prod"
  project_name              = "flm-cloud-native-platform"
  vpc_cidr                  = "10.1.0.0/16"
  public_subnet_cidrs       = ["10.1.1.0/24", "10.1.2.0/24"]
  private_subnet_cidrs      = ["10.1.11.0/24", "10.1.12.0/24"]
  eks_version               = "1.36"
  node_instance_types       = ["t3.large"]
  node_desired_size         = 3
  node_min_size             = 2
  node_max_size             = 6
  enable_ecr_image_scanning = true

  additional_tags = {
    CostCenter  = "engineering"
    Owner       = "platform-team"
    Criticality = "high"
  }
}