import unittest
import os
#from sikuli import *
from org.sikuli.script.android import AndroidScreen
from org.sikuli.script import Settings

class TestAndroidBasic(unittest.TestCase):

   @classmethod
   def setUpClass(cls):
      cls.s = AndroidScreen()
      assert cls.s != None
      cls.dev = cls.s.getRobot().getDevice()
      Settings.BundlePath = os.getcwd() + "/test-res"
      Settings.MoveMouseDelay = 0
      Settings.AutoWaitTimeout = 10

   def testBounds(self):
      bounds = self.s.getBounds()
      assert(bounds.x == 0)
      assert(bounds.y == 0)
      assert(bounds.width > 0)
      assert(bounds.height > 0)

   def testCapture(self):
      img = self.s.capture()
      assert img != None

#   def testFind(self):
#      print "bundle: ", Settings.BundlePath
#      m = self.s.find("lock.png")
#      assert m != None

   def testDrag(self):
      if not self.s.exists("lock.png"):
         self.dev.press('KEYCODE_POWER', 'DOWN_AND_UP')
      self.s.dragDrop("lock.png", "speaker.png")

   def testType(self):
      self.dev.press('KEYCODE_HOME', 'DOWN_AND_UP')
      self.s.click("google.png")
      self.s.type("sikuli")
      assert self.s.exists("search-sikuli.png")

