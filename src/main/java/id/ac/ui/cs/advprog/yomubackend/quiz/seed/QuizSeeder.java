package id.ac.ui.cs.advprog.yomubackend.quiz.seed;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.*;
import id.ac.ui.cs.advprog.yomubackend.quiz.repository.ReadingRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

@Profile("!test")
@Component
public class QuizSeeder {

    private final ReadingRepository readingRepository;

    public QuizSeeder(ReadingRepository readingRepository) {
        this.readingRepository = readingRepository;
    }

    @PostConstruct
    public void seed() {
        if (readingRepository.count() > 0) return;

        readingRepository.saveAll(List.of(
                buildReading(
                        "Maraknya Hoaks di Era Digital",
                        "BERITA",
                        """
                        Di era digital saat ini, penyebaran informasi berlangsung sangat cepat melalui media sosial. \
                        Sayangnya, tidak semua informasi yang beredar dapat dipercaya. Hoaks atau berita palsu \
                        kerap menyebar lebih cepat dibandingkan berita yang valid karena kontennya dirancang \
                        untuk memancing emosi pembaca. Untuk itu, masyarakat perlu memverifikasi setiap informasi \
                        yang diterima melalui sumber terpercaya sebelum meneruskannya kepada orang lain.""",
                        List.of(
                                buildQuestion("Apa yang dimaksud dengan hoaks?",
                                        List.of(
                                                buildOption("Informasi yang tidak benar dan menyesatkan", true),
                                                buildOption("Berita yang diterbitkan oleh media resmi", false),
                                                buildOption("Laporan ilmiah yang belum diverifikasi", false),
                                                buildOption("Iklan berbayar di media sosial", false)
                                        )),
                                buildQuestion("Mengapa hoaks menyebar lebih cepat daripada berita yang valid?",
                                        List.of(
                                                buildOption("Karena dirancang untuk memancing emosi pembaca", true),
                                                buildOption("Karena disebarkan oleh media terpercaya", false),
                                                buildOption("Karena mengandung data statistik yang lengkap", false),
                                                buildOption("Karena menggunakan bahasa ilmiah yang mudah dipahami", false)
                                        )),
                                buildQuestion("Apa langkah yang disarankan teks untuk menghadapi hoaks?",
                                        List.of(
                                                buildOption("Memverifikasi informasi melalui sumber terpercaya sebelum meneruskannya", true),
                                                buildOption("Langsung membagikan informasi agar orang lain ikut waspada", false),
                                                buildOption("Menghindari penggunaan media sosial sepenuhnya", false),
                                                buildOption("Melaporkan semua berita kepada pemerintah", false)
                                        ))
                        )
                ),
                buildReading(
                        "Timnas Indonesia Melaju ke Babak Final",
                        "OLAHRAGA",
                        """
                        Timnas sepak bola Indonesia berhasil melaju ke babak final turnamen regional setelah \
                        mengalahkan Malaysia dengan skor 2-1 pada pertandingan semifinal yang berlangsung di \
                        Stadion Gelora Bung Karno, Jakarta. Gol kemenangan dicetak oleh Egy Maulana Vikri \
                        pada menit ke-87 setelah sebelumnya kedudukan sempat imbang 1-1. Kemenangan ini \
                        disambut antusias oleh puluhan ribu pendukung yang hadir langsung di stadion.""",
                        List.of(
                                buildQuestion("Berapa skor akhir pertandingan semifinal Indonesia vs Malaysia?",
                                        List.of(
                                                buildOption("2-1 untuk Indonesia", true),
                                                buildOption("1-0 untuk Indonesia", false),
                                                buildOption("2-0 untuk Indonesia", false),
                                                buildOption("1-1 berakhir imbang", false)
                                        )),
                                buildQuestion("Di mana pertandingan semifinal tersebut berlangsung?",
                                        List.of(
                                                buildOption("Stadion Gelora Bung Karno, Jakarta", true),
                                                buildOption("Stadion Utama Riau, Pekanbaru", false),
                                                buildOption("Stadion Manahan, Solo", false),
                                                buildOption("Stadion Si Jalak Harupat, Bandung", false)
                                        )),
                                buildQuestion("Siapa yang mencetak gol kemenangan Indonesia?",
                                        List.of(
                                                buildOption("Egy Maulana Vikri", true),
                                                buildOption("Witan Sulaeman", false),
                                                buildOption("Marselino Ferdinan", false),
                                                buildOption("Pratama Arhan", false)
                                        ))
                        )
                ),
                buildReading(
                        "Kecerdasan Buatan dalam Kehidupan Sehari-hari",
                        "TEKNOLOGI",
                        """
                        Kecerdasan buatan (AI) kini telah menjadi bagian tak terpisahkan dari kehidupan sehari-hari. \
                        Mulai dari asisten virtual di ponsel pintar, rekomendasi konten di platform streaming, \
                        hingga sistem deteksi penipuan pada transaksi perbankan, AI bekerja di balik layar \
                        untuk meningkatkan kenyamanan dan keamanan pengguna. Meskipun membawa banyak manfaat, \
                        kehadiran AI juga memunculkan kekhawatiran terkait privasi data dan potensi \
                        penggunaannya yang tidak etis.""",
                        List.of(
                                buildQuestion("Apa saja contoh penerapan AI yang disebutkan dalam teks?",
                                        List.of(
                                                buildOption("Asisten virtual, rekomendasi konten, dan deteksi penipuan perbankan", true),
                                                buildOption("Robot industri, mobil otonom, dan drone pengiriman", false),
                                                buildOption("Kamera pengawas, lampu pintar, dan kulkas otomatis", false),
                                                buildOption("Mesin pencari, email otomatis, dan cetak 3D", false)
                                        )),
                                buildQuestion("Apa kekhawatiran yang muncul akibat kehadiran AI menurut teks?",
                                        List.of(
                                                buildOption("Privasi data dan potensi penggunaan yang tidak etis", true),
                                                buildOption("Meningkatnya angka pengangguran secara masif", false),
                                                buildOption("Ketergantungan manusia terhadap listrik", false),
                                                buildOption("Mahalnya biaya perangkat keras AI", false)
                                        ))
                        )
                ),
                buildReading(
                        "Pentingnya Literasi Informasi di Era Modern",
                        "BUDAYA",
                        """
                        Literasi informasi adalah kemampuan untuk mengenali kapan informasi dibutuhkan, \
                        menemukan, mengevaluasi, dan menggunakannya secara efektif. Di tengah banjir informasi \
                        saat ini, literasi informasi menjadi keterampilan esensial yang harus dimiliki oleh \
                        setiap individu. Tanpa kemampuan ini, seseorang mudah terjebak dalam lingkaran \
                        misinformasi yang dapat memengaruhi keputusan sehari-hari, mulai dari pilihan \
                        konsumsi hingga pandangan politik.""",
                        List.of(
                                buildQuestion("Apa yang dimaksud dengan literasi informasi dalam teks?",
                                        List.of(
                                                buildOption("Kemampuan mengenali, menemukan, mengevaluasi, dan menggunakan informasi secara efektif", true),
                                                buildOption("Kemampuan membaca dan menulis dalam berbagai bahasa asing", false),
                                                buildOption("Kemampuan mengoperasikan perangkat teknologi informasi", false),
                                                buildOption("Kemampuan menyebarkan informasi kepada banyak orang", false)
                                        )),
                                buildQuestion("Apa dampak yang dapat terjadi jika seseorang tidak memiliki literasi informasi?",
                                        List.of(
                                                buildOption("Mudah terjebak dalam lingkaran misinformasi yang memengaruhi keputusan sehari-hari", true),
                                                buildOption("Tidak mampu menggunakan media sosial dengan baik", false),
                                                buildOption("Kesulitan belajar bahasa pemrograman", false),
                                                buildOption("Tidak dapat mengakses internet secara bebas", false)
                                        ))
                        )
                )
        ));
    }

    private Reading buildReading(String title, String category, String content, List<Question> questions) {
        Reading reading = new Reading();
        reading.setTitle(title);
        reading.setCategory(category);
        reading.setContent(content);
        questions.forEach(q -> q.setReading(reading));
        reading.setQuestions(questions);
        return reading;
    }

    private Question buildQuestion(String text, List<Option> options) {
        Question question = new Question();
        question.setQuestionText(text);
        options.forEach(o -> o.setQuestion(question));
        question.setOptions(options);
        return question;
    }

    private Option buildOption(String text, boolean correct) {
        Option option = new Option();
        option.setOptionText(text);
        option.setCorrect(correct);
        return option;
    }
}
