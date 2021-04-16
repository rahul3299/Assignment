output "public_ip" {
  value = "${aws_instance.rahul-terraform.public_dns}"
}
