import random

from django.conf import settings
from django.core.mail import send_mail
from django.shortcuts import HttpResponse
from . import models

# Create your views here.


def send_email(request):
    if request.method == "POST":
        # 获取邮箱
        email = request.POST.get("email", "")
        # 生成验证码
        code = "%d%d%d%d" % (random.randint(0, 9), random.randint(0, 9), random.randint(0, 9),
                             random.randint(0, 9))
        send_mail('UAV验证码', '您的验证码是：%s' % code, settings.EMAIL_HOST_USER,
                  [email, ], fail_silently=False)
        return HttpResponse(code)   # 将验证码发送回去


def register(request):
    if request.method == "POST":
        # 获取邮箱跟密码
        email = request.POST.get("email")
        password = request.POST.get("password")
        # 加密 后续做

        # 将密码存起来
        models.User.objects.create(email=email, password=password)
        return HttpResponse("1")


def test(request):
    print("============================")
    print("***************************")
    return HttpResponse("OK")


def login(request):
    """
    0 邮箱未注册
    1 密码正确
    2 密码错误
    :param request:
    :return:
    """
    if request.method == "POST":
        email = request.POST.get("email")
        password = request.POST.get("password")
        # 获取email对应的用户
        user = models.User.objects.filter(email=email)
        if len(user) is 0:
            # 该邮箱未注册
            return HttpResponse("0")
        # 取出密码
        data_password = user[0].password
        # 对用户传过来的密码进行加密 后续做

        # 比对密码
        if data_password == password:
            # 密码正确
            return HttpResponse("1")
        # 密码错误
        return HttpResponse("2")
