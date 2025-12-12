NUEVAS IMPLEMENTACIONES AL PROGRAMA:

1. Nueva Interfaz Gráfica de Usuario (GUI)

2. Selección de Fecha: Posibilidad de seleccionar la fecha deseada a la hora de mostrar el valor de una divisa.

3. Nuevo modo "See Evolution": Permite comparar el valor de una divisa respecto a otra entre dos años, pudiendo
ajustar que años queremos y cada cuantos años queremos hacer la comparación.

4. Control de errores: Se añaden dos excepciones:
   
   a. ExchangeRateConnectionException: Se produce cuando no se logra conectar con la API.

   b. ExchangeRateReadingException: Se produce cuando no se logra obtener los datos. Por ejemplo, algunas divisas no tienen datos para todos los años que permite seleccionar el progama, en esos casos, salta esta excepción.
