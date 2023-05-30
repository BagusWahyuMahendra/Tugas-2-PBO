# API Pada Database E Commerce menggunakan Java OOP

API (Application Programming Interface) adalah sekumpulan aturan dan protokol yang digunakan oleh perangkat lunak untuk berinteraksi dengan satu sama lain. API memungkinkan aplikasi atau sistem untuk berkomunikasi dan bertukar data dengan aplikasi atau sistem lain.


Contoh umum API termasuk API web seperti RESTful API, SOAP API, API database seperti JDBC, API sistem operasi seperti Windows API, dan banyak lagi. API juga sering digunakan dalam pengembangan aplikasi mobile, integrasi platform, pengolahan data, dan komunikasi antar sistem secara umum.


Pada program java OOP (Object Oriented Programming) kali ini, API bertugas untuk mengakses dan memanipulasi data pada database dengan nama db_ecommerce yang dibuat menggunakan SQLite. Pada db_ecommerce ini terdapat beberapa entitas, seperti tb_users, tb_address, tb_products, tb_orders, tb_reviews, dan tb_order_details. Keenam entitas tersebut saling berhubungan atau berelasi satu sama lain yang dapat dilihat pada PDM (Physical Data Model) berikut ini.


![Screenshot 2023-05-30 131641](https://github.com/BagusWahyuMahendra/Tugas-2-PBO/assets/114908291/9cb4ddff-b40d-42c3-b9c6-d489b054f850)


Backend API java ini merupakan program yang digunakan untuk dapat menampilkan, memperbaharui, menambahkan, dan menghapus data yang terdapat pada tabel di db_ecommerce. Untuk mengakses dan memanipulasi database dalam program Java, dapat menggunakan JDBC (Java Database Connectivity). 


JDBC adalah API yang menyediakan koneksi dan operasi terhadap berbagai jenis database yang kompatibel dengan JDBC. JBDC dapat digunakan dengan mendownload driver JBDC ke dalam project yang mana file driver berupa file jar. 


Selain JBDC, pada program ini juga menggunakan JSON (JavaScript Object Notation) yang digunakan untuk pertukaran data antara aplikasi Java dengan aplikasi atau layanan lain yang menggunakan format data JSON. JSON dapat digunakan jika sudah menambahkan library JSON ke project yang dibuat berupa file jar.
