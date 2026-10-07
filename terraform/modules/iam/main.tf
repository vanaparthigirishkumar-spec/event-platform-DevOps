data "aws_iam_policy_document" "eks_cluster_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["eks.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "eks_cluster" {
  name               = "${var.name_prefix}-eks-cluster-role"
  assume_role_policy = data.aws_iam_policy_document.eks_cluster_assume_role.json
  description        = "IAM role for EKS cluster control plane"
  tags               = var.tags
}

resource "aws_iam_role_policy_attachment" "eks_cluster_policy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSClusterPolicy"
  role       = aws_iam_role.eks_cluster.name
}

resource "aws_iam_role_policy_attachment" "eks_vpc_resource_controller" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSVPCResourceController"
  role       = aws_iam_role.eks_cluster.name
}

data "aws_iam_policy_document" "eks_node_assume_role" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "eks_node" {
  name               = "${var.name_prefix}-eks-node-role"
  assume_role_policy = data.aws_iam_policy_document.eks_node_assume_role.json
  description        = "IAM role for EKS worker nodes"
  tags               = var.tags
}

resource "aws_iam_role_policy_attachment" "eks_worker_node_policy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKSWorkerNodePolicy"
  role       = aws_iam_role.eks_node.name
}

resource "aws_iam_role_policy_attachment" "eks_cni_policy" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEKS_CNI_Policy"
  role       = aws_iam_role.eks_node.name
}

resource "aws_iam_role_policy_attachment" "ecr_readonly" {
  policy_arn = "arn:aws:iam::aws:policy/AmazonEC2ContainerRegistryReadOnly"
  role       = aws_iam_role.eks_node.name
}

resource "aws_iam_role_policy_attachment" "cloudwatch_logs" {
  policy_arn = "arn:aws:iam::aws:policy/CloudWatchLogsFullAccess"
  role       = aws_iam_role.eks_node.name
}

data "aws_iam_policy_document" "ecr_access" {
  statement {
    sid = "ECRAccess"
    actions = [
      "ecr:GetAuthorizationToken",
      "ecr:BatchCheckLayerAvailability",
      "ecr:GetDownloadUrlForLayer",
      "ecr:BatchGetImage",
      "ecr:InitiateLayerUpload",
      "ecr:UploadLayerPart",
      "ecr:CompleteLayerUpload",
      "ecr:PutImage"
    ]
    resources = ["*"]
  }
}

resource "aws_iam_policy" "ecr_access" {
  name   = "${var.name_prefix}-ecr-access"
  policy = data.aws_iam_policy_document.ecr_access.json
  tags   = var.tags
}

data "aws_iam_policy_document" "cloudwatch_access" {
  statement {
    sid = "CloudWatchAccess"
    actions = [
      "logs:CreateLogGroup",
      "logs:CreateLogStream",
      "logs:PutLogEvents",
      "logs:DescribeLogGroups",
      "logs:DescribeLogStreams"
    ]
    resources = ["arn:aws:logs:*:*:*"]
  }
}

resource "aws_iam_policy" "cloudwatch_access" {
  name   = "${var.name_prefix}-cloudwatch-access"
  policy = data.aws_iam_policy_document.cloudwatch_access.json
  tags   = var.tags
}

data "aws_iam_policy_document" "secrets_manager_access" {
  statement {
    sid = "SecretsManagerAccess"
    actions = [
      "secretsmanager:GetSecretValue",
      "secretsmanager:DescribeSecret"
    ]
    resources = ["*"]
    condition {
      test     = "StringEquals"
      variable = "secretsmanager:ResourceTag/ManagedBy"
      values   = ["terraform"]
    }
  }
}

resource "aws_iam_policy" "secrets_manager_access" {
  name   = "${var.name_prefix}-secrets-manager-access"
  policy = data.aws_iam_policy_document.secrets_manager_access.json
  tags   = var.tags
}

data "aws_iam_policy_document" "ssm_access" {
  statement {
    sid = "SSMAccess"
    actions = [
      "ssm:GetParameter",
      "ssm:GetParameters",
      "ssm:GetParametersByPath"
    ]
    resources = ["arn:aws:ssm:*:*:parameter/*"]
    condition {
      test     = "StringEquals"
      variable = "aws:ResourceTag/ManagedBy"
      values   = ["terraform"]
    }
  }
}

resource "aws_iam_policy" "ssm_access" {
  name   = "${var.name_prefix}-ssm-access"
  policy = data.aws_iam_policy_document.ssm_access.json
  tags   = var.tags
}