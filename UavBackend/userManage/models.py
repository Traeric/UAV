from django.db import models

# Create your models here.


class User(models.Model):
    """用户表"""
    email = models.EmailField(verbose_name="邮箱", unique=True)
    password = models.CharField(max_length=16, verbose_name="密码")
    avatar = models.CharField(max_length=32, verbose_name="头像", default="/static/assets/img/profile.jpg")
    nick = models.CharField(max_length=8, verbose_name="昵称", default="游客")
    key_words = models.CharField(max_length=128, verbose_name="当前用户拥有的关键字id，以逗号为间隔", default="")


class VoiceAssistantKeyWord(models.Model):
    """语音助手关键字"""
    name = models.CharField(max_length=16, verbose_name="关键字名称", null=False, blank=False)
    key_word = models.CharField(max_length=128, verbose_name="关键字列表，以逗号为间隔", null=False, blank=False)
    func_name = models.CharField(max_length=32, verbose_name="执行的方法名", default="null")
    code = models.TextField(verbose_name="要执行的代码", default="null")
    feedback = models.CharField(max_length=32, verbose_name="语音助手反馈的语音", default="好的")
    users = models.ManyToManyField(to=User)    # 多对多关联字段


