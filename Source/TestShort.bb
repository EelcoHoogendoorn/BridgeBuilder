Global X

File = WriteFile("Test Short")
X = 100
Y = -100

WriteShort File, X
WriteShort File, Y

CloseFile(File)

File = ReadFile("Test Short")
X = ReadShort(File)
Y = ReadShort(File)

Print X
Print Y

WaitKey()

End