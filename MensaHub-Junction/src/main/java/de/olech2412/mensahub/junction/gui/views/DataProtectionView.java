package de.olech2412.mensahub.junction.gui.views;

import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PageTitle("MensaHub-Datenschutz")
@Route(value = "datenschutzerklärung")
@PermitAll
public class DataProtectionView extends VerticalLayout implements BeforeEnterObserver {

    Logger logger = LoggerFactory.getLogger(DataProtectionView.class);

    public DataProtectionView() {
        add(new H1("Datenschutzerklärung"));
        add(new H2("1. Datenschutz auf einen Blick"));
        add(new H3("Allgemeine Hinweise"));
        add(new VerticalLayout(new Span("Die folgenden Hinweise geben einen einfachen Überblick darüber, was mit Ihren personenbezogenen Daten\n" +
                "passiert, wenn Sie diese Website besuchen. Personenbezogene Daten sind alle Daten, mit denen Sie\n" +
                "persönlich identifiziert werden können. Ausführliche Informationen zum Thema Datenschutz entnehmen\n" +
                "Sie unserer unter diesem Text aufgeführten Datenschutzerklärung.\n")));
        add(new H3("Datenerfassung auf dieser Website"));
        add(new VerticalLayout(new H4("Wie erfassen wir Ihre Daten?"), new Span("Ihre Daten werden zum einen dadurch erhoben, dass Sie uns diese mitteilen. Hierbei kann es sich z. B. um\n" +
                "Daten handeln, die Sie in ein Kontaktformular eingeben.\n" +
                "Andere Daten werden automatisch oder nach Ihrer Einwilligung beim Besuch der Website durch unsere ITSysteme erfasst. Das sind vor allem technische Daten (z. B. Internetbrowser, Betriebssystem oder Uhrzeit\n" +
                "des Seitenaufrufs). Die Erfassung dieser Daten erfolgt automatisch, sobald Sie diese Website betreten.")));
        add(new H3("Wofür nutzen wir Ihre Daten?"));
        add(new VerticalLayout(new Span("Ein Teil der Daten wird erhoben, um eine fehlerfreie Bereitstellung der Website zu gewährleisten. Andere Daten\n" +
                "können zur Analyse Ihres Nutzerverhaltens verwendet werden.")));
        add(new H3("Welche Rechte haben Sie bezüglich Ihrer Daten?"));
        add(new VerticalLayout(new Span("Sie haben jederzeit das Recht unentgeltlich Auskunft über Herkunft, Empfänger und Zweck Ihrer gespeicherten\n" +
                "personenbezogenen Daten zu erhalten. Sie haben außerdem ein Recht, die Berichtigung, Sperrung oder Löschung\n" +
                "dieser Daten zu verlangen. Hierzu sowie zu weiteren Fragen zum Thema Datenschutz können Sie sich jederzeit\n" +
                "über die im Impressum aufgeführten Kontaktmöglichkeiten an uns wenden. Des Weiteren steht Ihnen ein Beschwerderecht bei der zuständigen Aufsichtsbehörde zu.")));
        add(new H2("2. Hosting"));
        add(new VerticalLayout(new Span("Diese Website wird bei einem externen Dienstleister gehostet (Hoster). Die personenbezogenen Daten, die\n" +
                "auf dieser Website erfasst werden, werden auf den Servern des Hosters gespeichert. Hierbei kann es sich v. a.\n" +
                "um IP-Adressen, Kontaktanfragen, Meta- und Kommunikationsdaten, Vertragsdaten, Kontaktdaten, Namen,\n" +
                "Webseitenzugriffe und sonstige Daten, die über eine Website generiert werden, handeln.\n" +
                "Der Einsatz des Hosters erfolgt zum Zwecke der Vertragserfüllung gegenüber unseren potenziellen und\n" +
                "bestehenden Kunden (Art. 6 Abs. 1 lit. b DSGVO) und im Interesse einer sicheren, schnellen und effizienten\n" +
                "Verarbeitung sowie Übertragung von personenbezogenen Daten (Art. 6 Abs. 1 lit. f DSGVO).\n" +
                "Unser Hoster wird Ihre Daten nur insoweit verarbeiten, wie dies zur Erfüllung seiner Leistungspflichten\n" +
                "nötig ist und unsere Weisungen in Bezug auf diese Daten befolgen.")));
        add(new H3("Der Server wird beim Anbieter Strato gehosted"));
        add(new VerticalLayout(new Span("Anbieter ist die Strato AG, Otto-Ostrowski-Straße 7, 10249 Berlin (nachfolgend „Strato“). Wenn Sie unsere\n" +
                "Website besuchen, erfasst Strato verschiedene Logfiles inklusive Ihrer IP-Adressen.\n" +
                "Weitere Informationen entnehmen Sie der Datenschutzerklärung von Strato:\n" +
                "https://www.strato.de/datenschutz/.\n" +
                "Die Verwendung von Strato erfolgt auf Grundlage von Art. 6 Abs. 1 lit. f DSGVO. Wir haben ein berechtigtes\n" +
                "Interesse an einer möglichst zuverlässigen Darstellung unserer Website. Sofern eine entsprechende\n" +
                "Einwilligung abgefragt wurde, erfolgt die Verarbeitung ausschließlich auf Grundlage von Art. 6 Abs. 1 lit. a\n" +
                "DSGVO und § 25 Abs. 1 TTDSG, soweit die Einwilligung die Speicherung von Cookies oder den Zugriff auf\n" +
                "Informationen im Endgerät des Nutzers (z. B. Device-Fingerprinting) im Sinne des TTDSG umfasst. Die\n" +
                "Einwilligung ist jederzeit widerrufbar.")));
        add(new H2("3. Allgemeine Hinweise und Pflichtinformationen"));
        add(new H3("Datenschutz"));
        add(new VerticalLayout(new Span("Die Betreiber dieser Seiten nehmen den Schutz Ihrer persönlichen Daten sehr ernst. Wir behandeln Ihre\n" +
                "personenbezogenen Daten vertraulich und entsprechend den gesetzlichen Datenschutzvorschriften sowie\n" +
                "dieser Datenschutzerklärung.\n" +
                "Wenn Sie diese Website benutzen, werden verschiedene personenbezogene Daten erhoben.\n" +
                "Personenbezogene Daten sind Daten, mit denen Sie persönlich identifiziert werden können. Die vorliegende\n" +
                "Datenschutzerklärung erläutert, welche Daten wir erheben und wofür wir sie nutzen. Sie erläutert auch, wie\n" +
                "und zu welchem Zweck das geschieht.\n" +
                "Wir weisen darauf hin, dass die Datenübertragung im Internet (z. B. bei der Kommunikation per E-Mail)\n" +
                "Sicherheitslücken aufweisen kann. Ein lückenloser Schutz der Daten vor dem Zugriff durch Dritte ist nicht\n" +
                "möglich.")));
        add(new H3("Hinweis zur verantwortlichen Stelle"));
        add(new VerticalLayout(new Span("Die verantwortliche Stelle für die Datenverarbeitung auf dieser Website ist:\n" +
                "Ole Einar Christoph\n" +
                "Mannheimer Straße 5-7\n" +
                "04209 Leipzig\n" +
                "Telefon: 015734489797\n" +
                "E-Mail: olechristoph2412@gmail.com\n" +
                "Verantwortliche Stelle ist die natürliche oder juristische Person, die allein oder gemeinsam mit anderen über\n" +
                "die Zwecke und Mittel der Verarbeitung von personenbezogenen Daten (z. B. Namen, E-Mail-Adressen o. Ä.)\n" +
                "entscheidet.")));
        add(new H3("Speicherdauer"));
        add(new VerticalLayout(new Span("Soweit innerhalb dieser Datenschutzerklärung keine speziellere Speicherdauer genannt wurde, verbleiben\n" +
                "Ihre personenbezogenen Daten bei uns, bis der Zweck für die Datenverarbeitung entfällt. Wenn Sie ein\n" +
                "berechtigtes Löschersuchen geltend machen oder eine Einwilligung zur Datenverarbeitung widerrufen,\n" +
                "werden Ihre Daten gelöscht, sofern wir keine anderen rechtlich zulässigen Gründe für die Speicherung Ihrer\n" +
                "personenbezogenen Daten haben (z. B. steuer- oder handelsrechtliche Aufbewahrungsfristen); im\n" +
                "letztgenannten Fall erfolgt die Löschung nach Fortfall dieser Gründe.")));
        add(new H3("Allgemeine Hinweise zu den Rechtsgrundlagen der Datenverarbeitung auf dieser\n" +
                "Website\n"));
        add(new VerticalLayout(new Span("Sofern Sie in die Datenverarbeitung eingewilligt haben, verarbeiten wir Ihre personenbezogenen Daten auf\n" +
                "Grundlage von Art. 6 Abs. 1 lit. a DSGVO bzw. Art. 9 Abs. 2 lit. a DSGVO, sofern besondere Datenkategorien\n" +
                "nach Art. 9 Abs. 1 DSGVO verarbeitet werden. Im Falle einer ausdrücklichen Einwilligung in die Übertragung\n" +
                "personenbezogener Daten in Drittstaaten erfolgt die Datenverarbeitung außerdem auf Grundlage von Art.\n" +
                "49 Abs. 1 lit. a DSGVO. Sofern Sie in die Speicherung von Cookies oder in den Zugriff auf Informationen in\n" +
                "Ihr Endgerät (z. B. via Device-Fingerprinting) eingewilligt haben, erfolgt die Datenverarbeitung zusätzlich\n" +
                "auf Grundlage von § 25 Abs. 1 TTDSG. Die Einwilligung ist jederzeit widerrufbar. Sind Ihre Daten zur\n" +
                "Vertragserfüllung oder zur Durchführung vorvertraglicher Maßnahmen erforderlich, verarbeiten wir Ihre\n" +
                "Daten auf Grundlage des Art. 6 Abs. 1 lit. b DSGVO. Des Weiteren verarbeiten wir Ihre Daten, sofern diese\n" +
                "zur Erfüllung einer rechtlichen Verpflichtung erforderlich sind auf Grundlage von Art. 6 Abs. 1 lit. c DSGVO.\n" +
                "Die Datenverarbeitung kann ferner auf Grundlage unseres berechtigten Interesses nach Art. 6 Abs. 1 lit. f\n" +
                "DSGVO erfolgen. Über die jeweils im Einzelfall einschlägigen Rechtsgrundlagen wird in den folgenden\n" +
                "Absätzen dieser Datenschutzerklärung informiert.")));
        add(new H3("Widerruf Ihrer Einwilligung zur Datenverarbeitung\n"));
        add(new VerticalLayout(new Span("Viele Datenverarbeitungsvorgänge sind nur mit Ihrer ausdrücklichen Einwilligung möglich. Sie können eine\n" +
                "bereits erteilte Einwilligung jederzeit widerrufen. Dazu reicht eine formlose Mitteilung per E-Mail an uns.\n" +
                "Die Rechtmäßigkeit der bis zum Widerruf erfolgten Datenverarbeitung bleibt vom Widerruf unberührt.")));
        add(new H3("Widerspruchsrecht gegen die Datenerhebung in besonderen Fällen sowie gegen\n" +
                "Direktwerbung (Art. 21 DSGVO)\n"));
        add(new VerticalLayout(new Span("WENN DIE DATENVERARBEITUNG AUF GRUNDLAGE VON ART. 6 ABS. 1 LIT. E ODER F DSGVO\n" +
                "ERFOLGT, HABEN SIE JEDERZEIT DAS RECHT, AUS GRÜNDEN, DIE SICH AUS IHRER BESONDEREN\n" +
                "SITUATION ERGEBEN, GEGEN DIE VERARBEITUNG IHRER PERSONENBEZOGENEN DATEN\n" +
                "WIDERSPRUCH EINZULEGEN; DIES GILT AUCH FÜR EIN AUF DIESE BESTIMMUNGEN GESTÜTZTES\n" +
                "PROFILING. DIE JEWEILIGE RECHTSGRUNDLAGE, AUF DENEN EINE VERARBEITUNG BERUHT,\n" +
                "ENTNEHMEN SIE DIESER DATENSCHUTZERKLÄRUNG. WENN SIE WIDERSPRUCH EINLEGEN,\n" +
                "WERDEN WIR IHRE BETROFFENEN PERSONENBEZOGENEN DATEN NICHT MEHR VERARBEITEN, ES\n" +
                "SEI DENN, WIR KÖNNEN ZWINGENDE SCHUTZWÜRDIGE GRÜNDE FÜR DIE VERARBEITUNG\n" +
                "NACHWEISEN, DIE IHRE INTERESSEN, RECHTE UND FREIHEITEN ÜBERWIEGEN ODER DIE\n" +
                "VERARBEITUNG DIENT DER GELTENDMACHUNG, AUSÜBUNG ODER VERTEIDIGUNG VON\n" +
                "RECHTSANSPRÜCHEN (WIDERSPRUCH NACH ART. 21 ABS. 1 DSGVO).\n" +
                "WERDEN IHRE PERSONENBEZOGENEN DATEN VERARBEITET, UM DIREKTWERBUNG ZU BETREIBEN,\n" +
                "SO HABEN SIE DAS RECHT, JEDERZEIT WIDERSPRUCH GEGEN DIE VERARBEITUNG SIE\n" +
                "BETREFFENDER PERSONENBEZOGENER DATEN ZUM ZWECKE DERARTIGER WERBUNG\n" +
                "EINZULEGEN; DIES GILT AUCH FÜR DAS PROFILING, SOWEIT ES MIT SOLCHER DIREKTWERBUNG IN\n" +
                "VERBINDUNG STEHT. WENN SIE WIDERSPRECHEN, WERDEN IHRE PERSONENBEZOGENEN DATEN\n" +
                "ANSCHLIESSEND NICHT MEHR ZUM ZWECKE DER DIREKTWERBUNG VERWENDET (WIDERSPRUCH\n" +
                "NACH ART. 21 ABS. 2 DSGVO).")));
        add(new H3("Beschwerderecht bei der zuständigen Aufsichtsbehörde\n"));
        add(new VerticalLayout(new Span("Im Falle von Verstößen gegen die DSGVO steht den Betroffenen ein Beschwerderecht bei einer\n" +
                "Aufsichtsbehörde, insbesondere in dem Mitgliedstaat ihres gewöhnlichen Aufenthalts, ihres\n" +
                "Arbeitsplatzes oder des Orts des mutmaßlichen Verstoßes zu. Das Beschwerderecht besteht unbeschadet\n" +
                "anderweitiger verwaltungsrechtlicher oder gerichtlicher Rechtsbehelfe.")));
        add(new H3("Recht auf Datenübertragbarkeit\n"));
        add(new VerticalLayout(new Span("Sie haben das Recht, Daten, die wir auf Grundlage Ihrer Einwilligung oder in Erfüllung eines\n" +
                "Vertrags automatisiert verarbeiten, an sich oder an einen Dritten in einem gängigen, maschinenlesbaren\n" +
                "Format aushändigen zu lassen. Sofern Sie die direkte Übertragung der Daten an einen anderen\n" +
                "Verantwortlichen verlangen, erfolgt dies nur, soweit es technisch machbar ist.")));
        add(new H3("Auskunft, Löschung und Berechtigung\n"));
        add(new VerticalLayout(new Span("Sie haben im Rahmen der geltenden gesetzlichen Bestimmungen jederzeit das Recht auf unentgeltliche\n" +
                "Auskunft über Ihre gespeicherten personenbezogenen Daten, deren Herkunft und Empfänger und den\n" +
                "zweck der Datenverarbeitung und ggf. ein Recht auf Berichtigung, Sperrung oder Löschung dieser\n" +
                "Daten. Hierzu sowie zu weiteren Fragen zum Thema personenbezogene Daten können Sie sich jederzeit\n" +
                "über die im Impressum angegebenen Kontaktmöglichkeiten an uns wenden.")));
        add(new H3("Recht auf Einschränkung der Verarbeitung\n"));
        add(new Span("Sie haben das Recht, die Einschränkung der Verarbeitung Ihrer personenbezogenen Daten zu verlangen.\n" +
                "Hierzu können Sie sich jederzeit an uns wenden. Das Recht auf Einschränkung der Verarbeitung besteht in\n" +
                "folgenden Fällen:\n" +
                "Wenn Sie die Richtigkeit Ihrer bei uns gespeicherten personenbezogenen Daten bestreiten, benötigen wir\n" +
                "in der Regel Zeit, um dies zu überprüfen. Für die Dauer der Prüfung haben Sie das Recht, die\n" +
                "Einschränkung der Verarbeitung Ihrer personenbezogenen Daten zu verlangen.\n" +
                "Wenn die Verarbeitung Ihrer personenbezogenen Daten unrechtmäßig geschah/geschieht, können Sie\n" +
                "statt der Löschung die Einschränkung der Datenverarbeitung verlangen.\n" +
                "Wenn wir Ihre personenbezogenen Daten nicht mehr benötigen, Sie sie jedoch zur Ausübung,\n" +
                "Verteidigung oder Geltendmachung von Rechtsansprüchen benötigen, haben Sie das Recht, statt der\n" +
                "Löschung die Einschränkung der Verarbeitung Ihrer personenbezogenen Daten zu verlangen.\n" +
                "Wenn Sie einen Widerspruch nach Art. 21 Abs. 1 DSGVO eingelegt haben, muss eine Abwägung zwischen\n" +
                "Ihren und unseren Interessen vorgenommen werden. Solange noch nicht feststeht, wessen Interessen\n" +
                "überwiegen, haben Sie das Recht, die Einschränkung der Verarbeitung Ihrer personenbezogenen Daten\n" +
                "zu verlangen.\n" +
                "Wenn Sie die Verarbeitung Ihrer personenbezogenen Daten eingeschränkt haben, dürfen diese Daten – von\n" +
                "ihrer Speicherung abgesehen – nur mit Ihrer Einwilligung oder zur Geltendmachung, Ausübung oder\n" +
                "Verteidigung von Rechtsansprüchen oder zum Schutz der Rechte einer anderen natürlichen oder\n" +
                "juristischen Person oder aus Gründen eines wichtigen öffentlichen Interesses der Europäischen Union oder\n" +
                "eines Mitgliedstaats verarbeitet werden."));
        add(new H3("SSL- bzw. TLS-Verschlüsselung\n"));
        add(new VerticalLayout(new Span("Diese Seite nutzt aus Sicherheitsgründen und zum Schutz der Übertragung vertraulicher Inhalte,\n" +
                "wie zum Beispiel Bestellungen oder Anfragen, die Sie an uns als Seitenbetreiber senden, eine SSL-\n" +
                "bzw. TLS-Verschlüsselung. Eine verschlüsselte Verbindung erkennen Sie daran, dass die Adresszeile des\n" +
                "Browsers von „http://“ auf „https://“ wechselt und an dem Schloss-Symbol in Ihrer Browserzeile.\n" +
                "Wenn die SSL- bzw. TLS-Verschlüsselung aktiviert ist, können die Daten, die Sie an uns übermitteln, nicht\n" +
                "von Dritten mitgelesen werden.")));
        add(new H2("4. Datenerfassung auf dieser Website\n"));
        add(new H3("Cookies\n"));
        add(new VerticalLayout(new Span("Unsere Internetseiten verwenden so genannte „Cookies“. Cookies sind kleine Datenpakete und richten auf\n" +
                "Ihrem Endgerät keinen Schaden an. Sie werden entweder vorübergehend für die Dauer einer Sitzung\n" +
                "(Session-Cookies) oder dauerhaft (permanente Cookies) auf Ihrem Endgerät gespeichert. Session-Cookies\n" +
                "werden nach Ende Ihres Besuchs automatisch gelöscht. Permanente Cookies bleiben auf Ihrem Endgerät\n" +
                "gespeichert, bis Sie diese selbst löschen oder eine automatische Löschung durch Ihren Webbrowser erfolgt.\n" +
                "Cookies können von uns (First-Party-Cookies) oder von Drittunternehmen stammen (sog. Third-PartyCookies). Third-Party-Cookies ermöglichen die Einbindung bestimmter Dienstleistungen von\n" +
                "Drittunternehmen innerhalb von Webseiten (z. B. Cookies zur Abwicklung von Zahlungsdienstleistungen).\n" +
                "Cookies haben verschiedene Funktionen. Zahlreiche Cookies sind technisch notwendig, da bestimmte\n" +
                "Webseitenfunktionen ohne diese nicht funktionieren würden (z. B. die Warenkorbfunktion oder die Anzeige\n" +
                "von Videos). Andere Cookies können zur Auswertung des Nutzerverhaltens oder zu Werbezwecken\n" +
                "verwendet werden.\n" +
                "Cookies, die zur Durchführung des elektronischen Kommunikationsvorgangs, zur Bereitstellung\n" +
                "bestimmter, von Ihnen erwünschter Funktionen (z. B. für die Warenkorbfunktion) oder zur Optimierung der\n" +
                "Website (z. B. Cookies zur Messung des Webpublikums) erforderlich sind (notwendige Cookies), werden auf\n" +
                "Grundlage von Art. 6 Abs. 1 lit. f DSGVO gespeichert, sofern keine andere Rechtsgrundlage angegeben wird.\n" +
                "Der Websitebetreiber hat ein berechtigtes Interesse an der Speicherung von notwendigen Cookies zur\n" +
                "technisch fehlerfreien und optimierten Bereitstellung seiner Dienste. Sofern eine Einwilligung zur\n" +
                "Speicherung von Cookies und vergleichbaren Wiedererkennungstechnologien abgefragt wurde, erfolgt die\n" +
                "Verarbeitung ausschließlich auf Grundlage dieser Einwilligung (Art. 6 Abs. 1 lit. a DSGVO und § 25 Abs. 1\n" +
                "TTDSG); die Einwilligung ist jederzeit widerrufbar.\n" +
                "Sie können Ihren Browser so einstellen, dass Sie über das Setzen von Cookies informiert werden und\n" +
                "Cookies nur im Einzelfall erlauben, die Annahme von Cookies für bestimmte Fälle oder generell ausschließen\n" +
                "sowie das automatische Löschen der Cookies beim Schließen des Browsers aktivieren. Bei der\n" +
                "Deaktivierung von Cookies kann die Funktionalität dieser Website eingeschränkt sein.\n" +
                "Welche Cookies und Dienste auf dieser Website eingesetzt werden, können Sie dieser\n" +
                "Datenschutzerklärung entnehmen.")));

    }


    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {
        logger.info("DataProtection View entered");
    }
}
