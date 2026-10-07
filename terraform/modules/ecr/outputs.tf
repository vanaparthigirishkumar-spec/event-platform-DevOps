output "repository_urls" {
  description = "Map of repository names to URLs"
  value       = { for k, v in aws_ecr_repository.repos : k => v.repository_url }
}

output "repository_arns" {
  description = "Map of repository names to ARNs"
  value       = { for k, v in aws_ecr_repository.repos : k => v.arn }
}

output "repository_names" {
  description = "Map of repository names"
  value       = { for k, v in aws_ecr_repository.repos : k => v.name }
}

output "repository_uris" {
  description = "Map of repository names to URIs (for docker push)"
  value       = { for k, v in aws_ecr_repository.repos : k => v.repository_url }
}