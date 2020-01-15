import random

from django.conf import settings
from django.core.mail import send_mail
from django.shortcuts import HttpResponse, render
from . import models
import urllib.request
import re

# Create your views here.


def send_email(request):
    if request.method == "POST":
        # 获取邮箱
        email = request.POST.get("email", "")
        # 检查邮箱是否已经存在
        if len(models.User.objects.filter(email=email)) != 0:
            return HttpResponse("0")
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


def news_list(request):
    # 爬取链接
    uav_url = "http://www.wrjzj.com/a/2.aspx"
    # 想要伪装的头部
    headers = {
        'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 '
                      '(KHTML, like Gecko) Chrome/70.0.3538.77 Safari/537.36',
    }
    # 构建请求对象
    req = urllib.request.Request(url=uav_url, headers=headers)
    content = urllib.request.urlopen(req).read().decode()
    # 匹配出文章
    pattern = re.compile(r'<li class="li02">.*?</li>', re.S)
    news_list_html = pattern.findall(content)
    news_list = []
    for item in news_list_html:
        # 获取标题
        title_pattern = re.compile(r'<div class="title">.*?<a .*?>.*?</a>.*?<a .*?>(.*?)</a>.*?</div>', re.S)
        title = title_pattern.findall(item)
        item_dict = {
            "title": title[0]
        }
        # 匹配图片
        image_pattern = re.compile(r'<img src="(.*?)" .*?/>', re.S)
        image = image_pattern.findall(item)
        item_dict["image"] = "http://www.wrjzj.com" + image[0]
        # 匹配摘要
        summary_pattern = re.compile(r'<div class="intro"><a .*?><img .*?></a>(.*?)...【<a .*?>详情</a>】</div>', re.S)
        summary = summary_pattern.findall(item)
        item_dict["summary"] = summary[0]
        # 匹配日期
        date_pattern = re.compile(r'<span class="time">(.*?)</span>', re.S)
        date = date_pattern.findall(item)
        item_dict['date'] = date[0]
        # 匹配查看人数
        view_pattern = re.compile(r'<span class="yue">(.*?)</span>', re.S)
        view = view_pattern.findall(item)
        item_dict['view'] = view[0]
        # 查看评论数
        command_pattern = re.compile(r'<span class="pl">(.*?)</span>', re.S)
        command = command_pattern.findall(item)
        item_dict['command'] = command[0]
        news_list.append(item_dict)
    return render(request, "remote_pages/news_list.html", {
        "news_list": news_list,
    })


