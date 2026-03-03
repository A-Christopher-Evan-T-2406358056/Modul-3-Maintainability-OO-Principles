# Laporan Refleksi

## Modul 1

### Refleksi 1

**Implementasi Backend dan Exception Handling**
Dimulai dari tingkat *low-level* atau *business logic* (serupa antara `removeById` dan `findById`), saya menggunakan
*custom checked exception* bernama `ProductNotFoundException`. Exception ini diterapkan pada fungsi *low-level*, di mana
tanggung jawab penanganan *exception* (handling) kemudian diteruskan ke *Controller*. Hal ini merupakan refleksi dari
pendekatan awal saya yang sebelumnya mengabaikan kasus sederhana seperti "produk tidak ditemukan" dan membiarkan program
mengalami terminasi begitu saja.

**Pemisahan Tanggung Jawab (Service vs. Repository)**
Saya mendiskusikan peran antara *Service* dan *Repository* serta berupaya agar interaksi keduanya terenkapsulasi dengan
baik. *Repository* difokuskan hanya untuk menerima atau memodifikasi data (persisitensi basis data). Awalnya, saya
langsung membuat fungsi *edit*, namun saya berusaha menjaga agar *Repository* tidak sekompleks *Service*. Setelah
melakukan *merge*, saya menyadari bahwa *Repository* seharusnya menangani seluruh fungsi CRUD. Dengan demikian,
*Service* dapat langsung memanggil fungsi `updateProduct`, alih-alih harus melakukan `findById` terlebih dahulu lalu
mengakses objek produk satu per satu.

**Penggunaan DTO (Data Transfer Object)**
Saya menemukan bahwa DTO adalah abstraksi data yang ideal untuk diterima oleh *Service* dari *Controller*. Meskipun saya
sudah menerapkannya pada ID untuk penghapusan dan data produk, saya belum mengubah *Service* untuk pembuatan (*created*)
karena bagian tersebut sudah disediakan oleh modul. Namun, jika direfleksikan kembali, bagian tersebut seharusnya
dimodifikasi agar sesuai dengan *best practice* pada Spring Boot.

**API dan Frontend**
Saya melakukan riset mengenai cara kerja *request mapping* dan *REST Controller* untuk membuat API. Saya menyadari bahwa
penggunaan AJAX untuk formulir sederhana sebenarnya adalah bentuk *overengineering*, sehingga formulir standar sudah
cukup.

Terkait fungsi *edit*, saya bereksperimen dengan *inline editing*. Saya menghadapi beberapa tantangan dalam proses ini.
Ide sederhananya adalah menggunakan *event* JavaScript `double click` untuk mengubah teks menjadi elemen *input*. Namun,
hal ini melibatkan penyesuaian lebar (*width*), *padding*, dan penyalinan isi teks. Karena terdapat beberapa kolom,
pendekatan awal saya adalah menyematkan ID dari *template engine* ke setiap sel. Namun, karena API membutuhkan ketiga
data sekaligus dan data saat ini (*current data*) sudah ada di tabel, saya menggunakan *query selector* untuk menemukan
baris (*row*) yang relevan, lalu mengambil nilai berdasarkan nama *field* datanya.

Pendekatan serupa saya terapkan untuk membuat API khusus tombol hapus (*delete*). Awalnya saya menggunakan DTO agar
*parsing* dari *body* JSON HTTP Request berjalan otomatis. Namun, saya menyadari bahwa untuk data sekecil ID, akan lebih
efisien jika diambil langsung dari parameter URL atau *header*, daripada harus melalui *body* request.

### Refleksi 2

**1. Mengenai Kecukupan Unit Test**
Untuk pertanyaan pertama, menurut saya cara menentukan kapan *unit test* dianggap cukup adalah dengan memahami definisi
*unit test* itu sendiri sebagai pengujian otomatis. Pola pikir yang digunakan adalah membandingkannya dengan apa yang
kita lakukan secara manual setelah menulis kode: "Apakah baris kode yang baru saya buat sudah aman?".

Memastikan *testing* sudah cukup memang sulit karena banyaknya kemungkinan kejadian pada perangkat lunak. Namun, standar
yang biasa digunakan adalah memikirkan setiap fitur dan mendaftar kemungkinan *bug* atau perilaku yang diharapkan dalam
situasi tertentu. Mengubah sudut pandang menjadi sisi pengguna (*user*) juga dapat membantu memprioritaskan apa yang
perlu diuji (terkait fungsionalitas dan kebutuhan dalam SDLC).

Perlu diingat bahwa 100% *code coverage* tidak menjamin bebas *bug*. *Unit test* yang buruk (misalnya sekadar
*copy-paste* output kode saat ini) akan tetap membuat kode tersebut "lulus" tes, meskipun tidak melakukan *assert* pada
bagian yang krusial. Selain itu, karena *code coverage* hanya memeriksa kode sumber saat ini, hal tersebut tidak
mencakup *bug* atau *error* yang mungkin timbul dari kode eksternal, seperti pustaka pihak ketiga maupun pustaka standar
Java.

**2. Mengenai Scope Functional Test**
Terkait pertanyaan kedua, menurut saya *test case* "memeriksa jumlah item di list" sebenarnya sudah termasuk dalam
cakupan kelas `CreateProductFunctionalTest.java`. Pengecekan jumlah item idealnya dilakukan setelah setiap fitur CRUD
dieksekusi. Refaktor ini membantu menyederhanakan kode (tanpa perlu menambahkan kelas baru) serta tetap menjaga
*coverage testing* yang baik. Hal ini menunjukkan bahwa untuk setiap kode yang ingin ditulis, kita sebaiknya memikirkan
cakupannya terlebih dahulu: apakah ini fitur baru, atau sekadar kode tambahan untuk melengkapi kode yang sudah ada.

## Modul 2

### Tautan

[Analisis Sonarcloud](https://sonarcloud.io/project/overview?id=A-Christopher-Evan-T-2406358056_Modul-1-Coding-Standards)
[Deployment Koyeb](progjut-progjut-321aaaa2.koyeb.app/)

### Code quality fixes

#### Dependency verification issues

Gradle berfungsi digunakan untuk manajemen depensensi. Namun setiap dependensi tersebut pada build yang baru akan
langsung unduh dari sumber internet lain. Hal ini bermasalah karena bisa saja library yang digunakan compromised,
bisa saja karena jaringan corrupted, ataupun pada sumber aslinya disisipkan program bahaya. Isu ini menonjol karena
penggunaan CI / CD github actions menggunakan mesin baru untuk aksi selanjutnya. Solusinya adalah menyetor kunci SHA
pada lokal sehingga dapat verifikasi dependensi yang digunakan environment lokal sama dengan production.

`./gradlew --write-verification-metadata pgp,sha256 --export-keys --refresh-keys clean build test check`

Gradle sudah memiliki fitur startup menabung kunci semua dependency tergantung task yang dijalankan. Maka saya jalankan
perintah tersebut dan mendapatkan berbagai berkas verification dalam direktori gradle yang digunakan untuk build
berikutnya agar pada production tetap pakai dependency yang sama.

`cat jquery.min.js | openssl dgst -sha384 -binary | openssl base64 -A`

Khusus penggunaan CDN untuk impor dependency pada template, saya benarkan dengan mengunduh pustakanya dan mencari
SHA keynya tersebut.

#### Unnecessary throw exception

Sonarcloud pindai kode dan saya sadar beberapa fungsi sebenarnya tidak perlu dispesifikasi throw Exception (karena
memang tidak mungkin melempar exception). Walau umumnya hanya terjadi dikode template, saya juga melakukan beberapa
kesalahan ini pada testcase tertentu dalam unit tests.

#### Misc code quality lain

Kesalahan lain meliputi penamaan variabel yang tidak sesuai standar java (yakni mudah dibenarkan dengan ganti nama
saja). Ada juga kesalahan memasuki nested fungsi yang dimana tidak perlu dalam javascript, penaruhan fungsi sebaik
mungkin
pada scope terluar. Hal ini mudah dilakukan secara tidak sengaja jika melakukan banyak nesting seperti pustaka jquery.
Dampak nested fungsi terlalu dalam tidak hanya pada performa, namun juga membuat kode tidak enak dilihat. Juga ada
beberapa kesalahan sederhana javascript seperti menggunakan global parseInt() ketimbang lebih spesifik
Number.parseInt().

### CI / CD

Look at your CI/CD workflows (GitHub)/pipelines (GitLab). Do you think the current
implementation has met the definition of Continuous Integration and Continuous
Deployment? Explain the reasons (minimum 3 sentences)!

Saya yakin dengan workflow repositori ini sudah implementasi CI / CD. Pertama implementasi workflow CI menggunakan hal
berikut

- SonarCloud : Menganalisis banyak hal terkait clean code, test coverage, security, dan linting
- Scorecards : Menganalisis spesifik terkait keamanan repositori
- Gradle : Memudahkan menjalankan environment build dan test yang sama pada deployment. Gradle build sudah otomatis
  menjalankan seluruh unit test.

Terlihat semua tools tersebut menjaga integritas kode secara otomatis (tidak perlu run unit test, baca kode, etc).
Dengan menggunakan github workflow untuk otomatis menjalankan tools tersebut, ini memberikan seolah - olah kode
dievaluasi secara
otomatis seakan proses development berjalan, sehingga mengikuti definisi Continuous dan Integration dalam CI. Terkait CD
juga sama,
dengan github workflow, proses menjalankan aplikasi untuk production dapat diotomasi. Sedangkan berbagai tools dapat
digunakan untuk
memudahkan deploymentnya (Dalam kasus ini saya menggunakan Koyeb)

## Modul 3

## Modul 3

### SRP (Single Responsibility Principle)

#### Penjelasan

Prinsip SOLID pertama yang saya aplikasikan adalah SRP (*Single Responsibility Principle*). Pelanggaran prinsip ini
terdapat pada fungsi `create()` di dalam `CarRepository` yang melakukan pengecekan dan pembuatan (generasi) UUID unik
untuk objek `Car` yang baru. Tanggung jawab ini seharusnya berada pada kelas `Car` itu sendiri. Oleh karena itu, saya
memindahkan logika pembuatan UUID ke dalam *constructor* kelas `Car`.

Selain itu, terdapat pelanggaran SRP lainnya, yaitu kelas `CarController` disimpan di dalam berkas yang sama dengan
`ProductController.java`. Hal ini membuat *separation of concerns* antara kedua kelas menjadi tidak jelas pada tingkat
struktur berkas. Sebagai *good practice*, kedua kelas tersebut dipecah ke dalam berkasnya masing-masing.

#### Advantage of Implementing

Navigasi kode di dalam proyek menjadi lebih mudah karena struktur kode yang lebih rapi. Selain itu, proses *unit
testing* juga menjadi lebih mudah karena bagian yang perlu diuji berukuran lebih atomik.

#### Disadvantage of Not Implementing

Navigasi kode cenderung jauh lebih sulit, di mana *developer* perlu membaca berkas file yang lebih panjang untuk mencari
fitur spesifik. Penulisan *unit test* juga menjadi jauh lebih sulit untuk kelas yang memikul terlalu banyak tanggung
jawab.

### LSP (Liskov Substitution Principle)

#### Penjelasan

Prinsip SOLID kedua yang saya aplikasikan adalah LSP (*Liskov Substitution Principle*). Pelanggarannya terlihat pada
`CarController` yang melakukan *inherit* (pewarisan) dari `ProductController`. LSP menyatakan bahwa sebuah *subclass*
harus bisa menggantikan *superclass*-nya. Dalam kasus ini, `CarController` tidak dapat menggantikan `ProductController`
karena *endpoint* dan *service* yang digunakan sudah berbeda.

Alasan lainnya adalah secara konseptual, `CarController` bukanlah *subtype* dari `ProductController`. Walaupun bisa
diargumentasikan bahwa `Car` adalah tipe dari `Product`, dalam konteks ini, *controller* dari keduanya jelas membutuhkan
implementasi yang berbeda. Sebagai perbaikannya, saya menghilangkan *keyword* `extends ProductController` pada kelas
`CarController`.

#### Advantage of Implementing

Perilaku aplikasi menjadi lebih dapat diprediksi dan stabil karena *subclass* dijamin dapat menggantikan *superclass*
tanpa merusak logika program. Hierarki kelas yang dibangun menjadi lebih masuk akal secara desain, dapat di-*reuse*
dengan aman, dan meminimalisasi kebutuhan penyesuaian kode di masa depan.

#### Disadvantage of Not Implementing

Aplikasi menjadi rentan terhadap *bug* atau *runtime error* yang tidak terduga ketika objek *subclass* dipanggil di
tempat yang mengharapkan *superclass*. Pengembang sering kali harus menambahkan pengecekan tipe secara manual (seperti
penggunaan `instanceof`) atau *handling* khusus yang membuat kode sulit di-*maintain*.

### DIP (Dependency Inversion Principle)

#### Penjelasan

Prinsip SOLID ketiga yang saya aplikasikan adalah DIP (*Dependency Inversion Principle*). Pelanggaran prinsip ini
terlihat pada `CarController` yang melakukan *injection* pada *concrete class* `CarServiceImpl`, bukan pada *interface*
-nya, yakni `CarService`.

Hal ini membuat *unit testing* menjadi lebih sulit dan dapat menyebabkan dampak perubahan kode pada `CarServiceImpl`
bocor ke `CarController`. Perbaikannya cukup sederhana, yaitu dengan mengganti tipe *field* yang di-`@Autowired` menjadi
`CarService`. Spring Boot akan secara otomatis mencari dan meng-*inject* kelas yang mengimplementasikan *interface*
tersebut.

#### Advantage of Implementing

Ketergantungan antar komponen menjadi lebih longgar (*loosely coupled*), sehingga *controller* tidak terikat pada detail
implementasi spesifik dari *service*. Proses *unit testing* menjadi jauh lebih mudah dan terisolasi karena *interface*
dapat di-*mock* tanpa hambatan. Selain itu, pergantian atau penambahan implementasi *service* di masa depan tidak akan
memaksa kita untuk mengubah kode pada *controller*.

#### Disadvantage of Not Implementing

Komponen sistem menjadi sangat terikat satu sama lain (*tightly coupled*), sehingga setiap perubahan pada kelas konkret
berisiko merusak atau memengaruhi logika di kelas tingkat tinggi yang menggunakannya. Pengujian kode (*testing*) menjadi
kaku dan sulit karena sistem dipaksa bergantung pada implementasi asli yang mungkin membutuhkan *setup* yang rumit,
sehingga menurunkan fleksibilitas dan skalabilitas aplikasi secara keseluruhan.
