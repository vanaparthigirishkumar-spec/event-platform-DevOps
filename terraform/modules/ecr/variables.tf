variable "name_prefix" {
  description = "Name prefix for repositories"
  type        = string
}

variable "tags" {
  description = "Tags for ECR repositories"
  type        = map(string)
}

variable "enable_image_scanning" {
  description = "Enable image scanning on push"
  type        = bool
  default     = true
}

variable "repositories" {
  description = "List of repository names to create"
  type        = list(string)
  default     = ["backend", "frontend"]
}