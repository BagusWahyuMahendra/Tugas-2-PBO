# API Pada Database E Commerce menggunakan Java OOP

API (Application Programming Interface) adalah sekumpulan aturan dan protokol yang digunakan oleh perangkat lunak untuk berinteraksi dengan satu sama lain. API memungkinkan aplikasi atau sistem untuk berkomunikasi dan bertukar data dengan aplikasi atau sistem lain.


Contoh umum API termasuk API web seperti RESTful API, SOAP API, API database seperti JDBC, API sistem operasi seperti Windows API, dan banyak lagi. API juga sering digunakan dalam pengembangan aplikasi mobile, integrasi platform, pengolahan data, dan komunikasi antar sistem secara umum.


# PDM Database E-Commerce
Pada program java OOP (Object Oriented Programming) kali ini, API bertugas untuk mengakses dan memanipulasi data pada database dengan nama db_ecommerce yang dibuat menggunakan SQLite. Pada db_ecommerce ini terdapat beberapa entitas, seperti tb_users, tb_address, tb_products, tb_orders, tb_reviews, dan tb_order_details. Keenam entitas tersebut saling berhubungan atau berelasi satu sama lain yang dapat dilihat pada PDM (Physical Data Model) berikut ini.


![Screenshot 2023-05-30 131641](https://github.com/BagusWahyuMahendra/Tugas-2-PBO/assets/114908291/9cb4ddff-b40d-42c3-b9c6-d489b054f850)


Backend API java ini merupakan program yang digunakan untuk dapat menampilkan, memperbaharui, menambahkan, dan menghapus data yang terdapat pada tabel di db_ecommerce. Untuk mengakses dan memanipulasi database dalam program Java, dapat menggunakan JDBC (Java Database Connectivity). 


JDBC adalah API yang menyediakan koneksi dan operasi terhadap berbagai jenis database yang kompatibel dengan JDBC. JBDC dapat digunakan dengan mendownload driver JBDC ke dalam project yang mana file driver berupa file jar. 


Selain JBDC, pada program ini juga menggunakan JSON (JavaScript Object Notation) yang digunakan untuk pertukaran data antara aplikasi Java dengan aplikasi atau layanan lain yang menggunakan format data JSON. JSON dapat digunakan jika sudah menambahkan library JSON ke project yang dibuat berupa file jar.



# Class Main, Database, Server, Response, dan Request
Pada program ini, terdapat beberapa Class yang didesain agar dapat berperan sebagai API, yaitu terdapat Class Main, Database, Server, Response, dan Request.
1. Pada Class Main, terdapat satu method main yang digunakan untuk mengatur port yang ingin digunakan dan berisikan penanganan rute dari path pengguna
2. Class Database berfungsi untuk mengkoneksikan java dengan database yang telah dibuat, yaitu database pada SQLite
3. Class Server berisikan method untuk membuat API KEY, data handler, mengirim respon (send response), dan method untuk memvalidasi API KEY
4. Class Request berisikan method getRequestData yang berfungsi untuk mendapatkan data permintaan (request data) dari objek HttpExchange. Method tersebut membaca input stream dari HttpExchange dan mengembalikan data permintaan dalam bentuk string.
5. Class Response berisikan banyak method yang digunakan untuk merespon berbagai path. Pada kelas ini terdapat beberapa Static Class di dalamnya, yaitu UsersHandler, ProductsHandler, OrdersHandler, ReviewsHandler. Di dalam Static Class tersebut berisikan beberapa method yang digunakan untuk menampilkan (GET), menambahkan (POST), memperbaharui (PUT), dan menghapus (DELETE) data di dalam entitas atau tabel yang ada.




# Contoh Pengujian API pada Database E-Commerce
Berikut merupakan contoh pengujian API pada UserHandler



1. Menampilkan seluruh user yang ada pada tabel users menggunakan : GET http://127.0.0.1:8002/users


![Screenshot 2023-05-30 223314](https://github.com/BagusWahyuMahendra/Tugas-2-PBO/assets/114908291/99d58ce3-f47c-4dc6-a2b0-e3fe96f1c8b3)


2. Menampilkan user sekalipun address juga muncul dengan berdasarkan id user menggunakan : GET http://127.0.0.1:8002/users/3


![Screenshot 2023-05-30 223325](https://github.com/BagusWahyuMahendra/Tugas-2-PBO/assets/114908291/7473ac8e-cd00-4e6f-a5c8-401328c8165e)



3. Menampilkan user berdasarkan type user menggunakan param : http://127.0.0.1:8002/users?type=seller


![Screenshot 2023-05-30 223414](https://github.com/BagusWahyuMahendra/Tugas-2-PBO/assets/114908291/aed01457-5d4c-4620-8076-880ceb099a9e)



4. Memperbaharui data pada salah satu user berdasarkan id menggunakan : PUT http://127.0.0.1:8002/users/addresses/10. Pada PUT harus mengisi bagian body yang formatnya JSON. Misalkan pada contoh berikut yang awalnya line 1 hanya Jl. Kenjeran, sekarang telah diperbaharui menjadi Jl. Kenjeran Timur


![Screenshot 2023-05-30 224340](https://github.com/BagusWahyuMahendra/Tugas-2-PBO/assets/114908291/5a29c4bc-6a24-4b72-ae28-585b6722f95b)



5. Menambahkan data ke dalam tabel users menggunakan : POST http://127.0.0.1:8002/users. Misalnya ingin menambahkan user baru dengan id user 11



![Screenshot 2023-05-30 223826](https://github.com/BagusWahyuMahendra/Tugas-2-PBO/assets/114908291/1b935c0d-0936-47fd-afe2-d041a64b5605)




6. Menghapus salah satu data pada tabel user berdasarkan id user, misalnya menghapus id 11 menggunakan : DELETE http://127.0.0.1:8002/users/11



![Screenshot 2023-05-30 224437](https://github.com/BagusWahyuMahendra/Tugas-2-PBO/assets/114908291/31a6d691-aa04-4069-ba74-346f21487e8b)



