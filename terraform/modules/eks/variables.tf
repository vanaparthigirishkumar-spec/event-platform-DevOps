variable "name_prefix" {
  description = "Name prefix for EKS cluster"
  type        = string
}

variable "tags" {
  description = "Tags for EKS resources"
  type        = map(string)
}

variable "vpc_id" {
  description = "VPC ID"
  type        = string
}

variable "private_subnet_ids" {
  description = "Private subnet IDs for EKS cluster and nodes"
  type        = list(string)
}

variable "eks_version" {
  description = "EKS Kubernetes version"
  type        = string
  default     = "1.29"
}

variable "cluster_role_arn" {
  description = "ARN of the EKS cluster IAM role"
  type        = string
}

variable "node_group_name" {
  description = "Name of the managed node group"
  type        = string
}

variable "node_role_arn" {
  description = "ARN of the EKS node IAM role"
  type        = string
}

variable "node_instance_types" {
  description = "EC2 instance types for node group"
  type        = list(string)
  default     = ["t3.medium"]
}

variable "node_desired_size" {
  description = "Desired number of nodes"
  type        = number
  default     = 2
}

variable "node_min_size" {
  description = "Minimum number of nodes"
  type        = number
  default     = 1
}

variable "node_max_size" {
  description = "Maximum number of nodes"
  type        = number
  default     = 4
}

variable "node_labels" {
  description = "Labels for nodes"
  type        = map(string)
  default     = {}
}