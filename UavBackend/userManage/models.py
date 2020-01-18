from django.db import models

# Create your models here.


class User(models.Model):
    email = models.EmailField(verbose_name="邮箱", unique=True)
    password = models.TextField(max_length=16, verbose_name="密码")
    avatar = models.TextField(max_length=32, verbose_name="头像")
    nick = models.TextField(max_length=8, verbose_name="昵称")

