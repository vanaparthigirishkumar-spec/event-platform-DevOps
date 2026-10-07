variable "name_prefix" {
  description = "Name prefix for resources"
  type        = string
}

variable "tags" {
  description = "Tags for IAM resources"
  type        = map(string)
}

variable "vpc_id" {
  description = "VPC ID for EKS cluster"
  type        = string
}