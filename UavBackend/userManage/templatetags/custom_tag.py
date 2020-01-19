from django import template

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

