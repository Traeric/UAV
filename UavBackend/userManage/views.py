import random

from django.conf import settings
from django.core.mail import send_mail
from django.shortcuts import HttpResponse, render
from . import models
import urllib.request
import re
import uuid
from . import utils


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
        return HttpResponse(code)  # 将验证码发送回去


def register(request):
    """
    注册
    :param request:
    :return:
    """
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
    id 密码正确
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
            return HttpResponse("no_register")
        # 取出密码
        data_password = user[0].password
        # 对用户传过来的密码进行加密 后续做

        # 比对密码
        if data_password == password:
            # 密码正确
            return HttpResponse(str(user[0].id))
        # 密码错误
        return HttpResponse("error")


def news_list(request):
    """
    无人机新闻页面
    :param request:
    :return:
    """
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
    news_arr = []
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
        news_arr.append(item_dict)
    return render(request, "remote_pages/news_list.html", {
        "news_list": news_arr,
    })


def login_back(request):
    """
    后台登录
    :param request:
    :return:
    """
    if request.method == "GET":
        return render(request, "login.html")


def get_uuid(request):
    """
    生成uuid
    :param request:
    :return:
    """
    # 生成uuid
    data = str(uuid.uuid1())
    # 将生成的uuid添加到map中
    settings.USER_INFO[data] = None
    return HttpResponse(data)


def refresh_login_status(request):
    """
    刷新登录状态
    :param request:
    :return:
    """
    if request.method == "POST":
        # 接收uuid
        user_id = request.POST.get("uuid")
        settings.USER_INFO[user_id] = {}
        return HttpResponse("ok")


def get_login_status(request):
    """
    获取用户扫描二维码状态
    :param request:
    :return:
    """
    # 获取uuid
    user_id = request.GET.get("uuid")
    if user_id in settings.USER_INFO:
        if settings.USER_INFO.get(user_id) is None:
            return HttpResponse("0")
        elif settings.USER_INFO.get(user_id):
            # 用户确认了登录
            # 登录成功，存到session中
            request.session["logined"] = True
            request.session["user_key"] = user_id
            return HttpResponse("3")
        return HttpResponse("1")
    else:
        return HttpResponse("2")


def cancel_login(request):
    """
    取消登录
    :param request:
    :return:
    """
    user_id = request.POST.get("uuid")
    if user_id in settings.USER_INFO:
        del settings.USER_INFO[user_id]
    return HttpResponse("ok")


def confirm_login(request):
    """
    确认登录
    :param request:
    :return:
    """
    if request.method == "POST":
        # 获取用户的id
        user_id = request.POST.get("user_id")
        # 获取uuid
        user_uuid = request.POST.get("uuid")
        # 查询出id对应的用户数据
        user_info = models.User.objects.filter(id=user_id)[0]
        # 存到用户字典中
        settings.USER_INFO[user_uuid] = user_info
        return HttpResponse("ok")


@utils.login_checked
def index(request, user_info):
    """
    首页
    :param user_info:
    :param request:
    :return:
    """
    return render(request, "index.html", {
        "user_info": user_info,
    })


@utils.login_checked
def key_word(request, user_info):
    """
    语音助手关键词配置
    :param user_info:
    :param request:
    :return:
    """
    # 获取所有的关键字信息
    key_words = models.VoiceAssistantKeyWord.objects.filter(users=user_info)
    # 获取用户拥有的关键字
    user_key_word = user_info.key_words
    return render(request, "key_word.html", {
        "user_info": user_info,
        "key_words": key_words,
        "user_key_word": user_key_word,
    })

