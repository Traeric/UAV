from django import template
import time

register = template.Library()


@register.filter
def user_have_current_key_word(current_id, user_key_word):
    """
    检查当前关键字的id该用户是否拥有
    :param current_id:
    :param user_key_word:
    :return:
    """
    return str(current_id) in user_key_word


@register.simple_tag
def convert_filename_to_date(filename):
    """
    时间转日期
    :param filename:
    :return:
    """
    # 截取日期
    date_timestamp = filename.rsplit(".", maxsplit=1)[0]
    time_struct = time.localtime(float(date_timestamp))
    return "{y}年{m}月{d}日 {h}:{min}:{s}".format(y=time_struct.tm_year, m=time_struct.tm_mon, d=time_struct.tm_mday,
                                               h=time_struct.tm_hour, min=time_struct.tm_min, s=time_struct.tm_sec)


@register.filter
def file_type(file_name, type_name):
    """
    识别文件类型
    :param type_name:
    :param file_name:
    :return:
    """
    return file_name.endswith(type_name)
