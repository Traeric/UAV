from django.conf import settings
from django.shortcuts import redirect
from django.urls import reverse


def login_checked(func):
    """
    检查登录的装饰器
    :param func:
    :return:
    """
    def inner(request):
        # 检查是否登录
        if request.session.get("logined"):
            # 获取用户信息
            try:
                user_info = settings.USER_INFO[request.session["user_key"]]
            except Exception as e:
                print(e)
                path = reverse('login_page')
                return redirect(path)
            # 执行函数
            return func(request, user_info)
        else:
            path = reverse('login_page')
            return redirect(path)
    return inner





