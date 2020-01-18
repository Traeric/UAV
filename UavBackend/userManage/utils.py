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
            # 执行函数
            return func(request)
        else:
            path = reverse('login_page')
            return redirect(path)
    return inner





