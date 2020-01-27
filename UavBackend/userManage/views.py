import random

from django.conf import settings
from django.core.mail import send_mail
from django.shortcuts import HttpResponse, render, redirect
from django.urls import reverse

from . import models
import urllib.request
import re
import uuid
import json
import os
import time
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
            return HttpResponse(json.dumps({
                "id": user[0].id,
                "nick": user[0].nick,
                "email": user[0].email,
                "avatar": user[0].avatar,
            }))
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


@utils.login_checked
def save_key_word(request, user_info):
    """
    保存关键字信息
    :param user_info:
    :param request:
    :return:
    """
    # 获取用户关键字信息
    if request.method == "POST":
        key_word_str = request.POST.get("key_word_str")
        models.User.objects.filter(id=user_info.id).update(key_words=key_word_str)
        # 更新user_info
        user_info.key_words = key_word_str
        return HttpResponse("1")


@utils.login_checked
def add_custom_key_word(request, user_info):
    """
    添加自定义的关键字
    :param request:
    :param user_info:
    :return:
    """
    return render(request, "custom_key_word.html", {
        "user_info": user_info,
    })


@utils.login_checked
def save_custom_key_word(request, user_info):
    """
    保存自定义关键字
    :param request:
    :param user_info:
    :return:
    """
    if request.method == "POST":
        # 获取关键字
        name = request.POST.get("name")
        key_words = request.POST.get("key_word")
        code = request.POST.get("code")
        feedback = request.POST.get("feedback")
        key = models.VoiceAssistantKeyWord.objects.create(name=name, key_word=key_words, code=code, feedback=feedback)
        # 关联用户
        key.users.add(user_info)
        path = reverse("key_word_config")
        return redirect(path)


def user_info_app(request):
    """
    app端展示用户信息
    :param request:
    :return:
    """
    user_info = models.User.objects.get(id=request.GET.get("id"))
    return render(request, "remote_pages/user_info.html", {
        "user_info": user_info,
    })


def app_get_key_word(request):
    """
    获取用户选中了的关键字
    :param request:
    :return:
    """
    # 获取用户id
    if request.method == "POST":
        uid = request.POST.get("id")
        # 查询出指定用户
        user = models.User.objects.filter(id=uid)
        key_word_list = user[0].key_words.split(",")
        keys_list = []
        for item in key_word_list:
            # 查询出指定的关键字信息
            key_object = models.VoiceAssistantKeyWord.objects.filter(id=item)
            keys_dict = {
                "key_word": key_object[0].key_word,
                "func_name": key_object[0].func_name,
                "code": key_object[0].code,
                "feedback": key_object[0].feedback,
            }
            keys_list.append(keys_dict)
        return HttpResponse(json.dumps(keys_list))


def upload_file(request):
    """
    上传文件
    :param request:
    :return:
    """
    if request.method == "POST":
        # 获取用户id
        uid = request.POST.get("id")
        # 查询用户邮箱
        email = models.User.objects.get(id=uid).email
        # 拼接路径
        path = os.path.join(settings.BASE_DIR, "static", "user_info", email)
        if not os.path.isdir(path):
            os.makedirs(path)
        for i in request.FILES:
            # 保存单个文件
            try:
                with open(os.path.join(path, "%s.%s" % (time.time(), request.FILES.get(i).name.rsplit(".", maxsplit=1)[1])),
                          'wb') as f:
                    for line in request.FILES.get(i).chunks():
                        f.write(line)
            except Exception as e:
                print(e)
                return HttpResponse("上传失败！")
    return HttpResponse("上传成功！")


@utils.login_checked
def display_image(request, user_info):
    """
    展示图片
    :param user_info:
    :param request:
    :return:
    """
    # 组织路径
    path = os.path.join(settings.BASE_DIR, "static", "user_info", user_info.email)
    # 获取该路径下所有的文件
    image_list = os.listdir(path)
    return render(request, "display_image.html", {
        "user_info": user_info,
        "image_list": image_list,
    })


@utils.login_checked
def view_account(request, user_info):
    """
    查看账户
    :param request:
    :param user_info:
    :return:
    """
    return render(request, "view_account.html", {
        'user_info': user_info,
    })


@utils.login_checked
def profile_account(request, user_info):
    """
    user_info
    :param request:
    :param user_info:
    :return:
    """
    return render(request, "profile_account.html", {
        'user_info': user_info,
    })


@utils.login_checked
def avatar_upload(request, user_info):
    """
    上传图片
    :param request:
    :param user_info:
    :return:
    """
    if request.method == "POST":
        avatar = request.FILES.get("avatar")
        # 创建路径
        path = os.path.join(settings.BASE_DIR, "static", "user_info", "%s-avatar" % user_info.email)
        if not os.path.isdir(path):
            os.makedirs(path)
        image_name = "{name}.{f_type}".format(name=user_info.email, f_type=avatar.name.rsplit(".", maxsplit=1)[1])
        f_name = os.path.join(path, image_name)
        # 保存头像
        with open(f_name, 'wb') as f:
            for item in avatar.chunks():
                f.write(item)
        # 保存头像路径到数据库
        (models.User.objects.filter(id=user_info.id)
         .update(avatar="/static/user_info/%s-avatar/%s" % (user_info.email, image_name)))
        # 更新user_info
        user_info.avatar = "/static/user_info/%s-avatar/%s" % (user_info.email, image_name)
    return HttpResponse("ok")


@utils.login_checked
def update_user_info(request, user_info):
    """
    更新用户信息
    :param request:
    :param user_info:
    :return:
    """
    if request.method == "POST":
        nick = request.POST.get("nick")
        email = request.POST.get("email")
        password = request.POST.get("password")
        (models.User.objects.filter(id=user_info.id).update(nick=nick, email=email, password=password))
        # 更新user_info
        user_info.nick = nick
        user_info.email = email
        user_info.password = password
    path = reverse(viewname="profile_account")
    return redirect(path)

