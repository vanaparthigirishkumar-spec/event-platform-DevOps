output "eks_cluster_role_arn" {
  description = "ARN of the EKS cluster IAM role"
  value       = aws_iam_role.eks_cluster.arn
}

output "eks_cluster_role_name" {
  description = "Name of the EKS cluster IAM role"
  value       = aws_iam_role.eks_cluster.name
}

output "eks_node_role_arn" {
  description = "ARN of the EKS node IAM role"
  value       = aws_iam_role.eks_node.arn
}

output "eks_node_role_name" {
  description = "Name of the EKS node IAM role"
  value       = aws_iam_role.eks_node.name
}

output "ecr_access_policy_arn" {
  description = "ARN of the ECR access policy"
  value       = aws_iam_policy.ecr_access.arn
}

output "cloudwatch_access_policy_arn" {
  description = "ARN of the CloudWatch access policy"
  value       = aws_iam_policy.cloudwatch_access.arn
}

output "secrets_manager_access_policy_arn" {
  description = "ARN of the Secrets Manager access policy"
  value       = aws_iam_policy.secrets_manager_access.arn
}

output "ssm_access_policy_arn" {
  description = "ARN of the SSM access policy"
  value       = aws_iam_policy.ssm_access.arn
}