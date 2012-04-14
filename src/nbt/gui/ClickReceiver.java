package nbt.gui;

public interface ClickReceiver {

  void clicked(int x, int z);

  String name();

  void setRadius(int newRadius);

  int radius();

}
