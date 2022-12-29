BEGIN Ifthenelse
READ(x),
READ(y),
  x := 1,
  y := 5,
  IF (x = 1) THEN 
   y := 2,
   ELSE
   y := 3,
  END,
  PRINT(y),
END
