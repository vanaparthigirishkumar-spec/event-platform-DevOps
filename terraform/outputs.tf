output "vpc_id" {
  description = "VPC ID"
  value       = module.vpc.vpc_id
}

output "vpc_cidr" {
  description = "VPC CIDR block"
  value       = module.vpc.vpc_cidr
}

output "public_subnet_ids" {
  description = "Public subnet IDs"
  value       = module.vpc.public_subnet_ids
}

output "private_subnet_ids" {
  description = "Private subnet IDs"
  value       = module.vpc.private_subnet_ids
}

output "availability_zones" {
  description = "Availability zones"
  value       = module.vpc.availability_zones
}

output "nat_gateway_id" {
  description = "NAT Gateway ID"
  value       = module.vpc.nat_gateway_id
}

output "nat_eip" {
  description = "NAT Gateway Elastic IP"
  value       = module.vpc.nat_eip
}

output "internet_gateway_id" {
  description = "Internet Gateway ID"
  value       = module.vpc.internet_gateway_id
}

output "eks_cluster_name" {
  description = "EKS cluster name"
  value       = module.eks.cluster_name
}

output "eks_cluster_endpoint" {
  description = "EKS cluster endpoint"
  value       = module.eks.cluster_endpoint
}

output "eks_cluster_arn" {
  description = "EKS cluster ARN"
  value       = module.eks.cluster_arn
}

output "eks_cluster_security_group_id" {
  description = "EKS cluster security group ID"
  value       = module.eks.cluster_security_group_id
}

output "eks_kubeconfig" {
  description = "EKS cluster kubeconfig"
  value       = module.eks.kubeconfig
  sensitive   = true
}

output "ecr_repository_urls" {
  description = "ECR repository URLs"
  value       = module.ecr.repository_urls
}

output "ecr_repository_arns" {
  description = "ECR repository ARNs"
  value       = module.ecr.repository_arns
}

output "eks_node_role_arn" {
  description = "EKS node role ARN"
  value       = module.iam.eks_node_role_arn
}

output "eks_cluster_role_arn" {
  description = "EKS cluster role ARN"
  value       = module.iam.eks_cluster_role_arn
}