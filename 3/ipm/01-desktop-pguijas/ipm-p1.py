#!/usr/bin/env python3

import locale
import gettext
from pathlib import Path
from controller import Controller
from model import Intervals
from view import View

if __name__ == '__main__':
    locale.setlocale(locale.LC_ALL, "") #locale default del usuario
    LOCALE_DIR = Path(__file__).parent / "locale"
    gettext.bindtextdomain('ipm-p1', LOCALE_DIR)
    gettext.textdomain('ipm-p1')

    controller = Controller(Intervals("127.0.0.1:5000"),View())
    controller.main()
