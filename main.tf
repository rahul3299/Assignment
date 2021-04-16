resource "aws_instance" "Rahul-terraform" {
  ami             = "ami-03d64741867e7bb94"
  instance_type   = "${var.instance_type}"
  key_name        = "${var.key_name}"
  vpc_security_group_ids = [ "${aws_security_group.instance.id}" ]
  lifecycle {
    create_before_destroy = true
  }

  provisioner "file" {
   
    connection {
      host = "${aws_instance.Rahul-terraform.public_ip}"
      type     = "ssh"
      user     = "ec2-user"
      private_key = "${file("rahul.pem")}"
      timeout = "2m"
    }
  }

}

resource "aws_security_group" "instance" {
  name = "test-sg"
  description = "Allow traffic for instances"

  ingress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = [ "0.0.0.0/0" ]
  }

  ingress {
    protocol    = "tcp"
    from_port   = 90
    to_port     = 90
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port = 0
    to_port = 65535
    protocol = "tcp"
    cidr_blocks = [ "0.0.0.0/0" ]
  }
}
