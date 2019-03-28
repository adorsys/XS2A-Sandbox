package de.adorsys.psd2.sandbox.certificate.service;

import com.nimbusds.jose.util.X509CertUtils;
import de.adorsys.psd2.sandbox.certificate.domain.IssuerData;
import de.adorsys.psd2.sandbox.certificate.domain.SubjectData;
import de.adorsys.psd2.sandbox.certificate.domain.CertificateRequest;
import de.adorsys.psd2.sandbox.certificate.domain.CertificateResponse;
import de.adorsys.psd2.sandbox.certificate.domain.NcaId;
import de.adorsys.psd2.sandbox.certificate.domain.NcaName;
import de.adorsys.psd2.sandbox.certificate.domain.PspRole;
import de.adorsys.psd2.sandbox.certificate.exception.CertificateException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERUTF8String;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x500.style.IETFUtils;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.qualified.QCStatement;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/*
 Was copied from static Sandbox
*/

@Slf4j
@Service
public class CertificateService {
    private static final String ISSUER_CERTIFICATE = "MyRootCA.pem";
    private static final String ISSUER_PRIVATE_KEY = "MyRootCA.key";
    private static final ASN1ObjectIdentifier ETSI_QC_STATEMENT = new ASN1ObjectIdentifier("0.4.0.19495.2");
    private static final String NCA_SHORT_NAME = "FAKENCA";

    private final IssuerData issuerData;

    CertificateService() {
        issuerData = generateIssuerData();
    }

    /**
     * Load x509 cert from classpath.
     *
     * @return X509Certificate
     */
    public X509Certificate getCertificateFromClassPath() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream is = loader.getResourceAsStream("certificates/" + CertificateService.ISSUER_CERTIFICATE);

        if (is == null) {
            throw new CertificateException("Could not find certificate in classpath");
        }

        try {
            byte[] bytes = IOUtils.toByteArray(is);
            return X509CertUtils.parse(bytes);
        } catch (IOException ex) {
            throw new CertificateException("Could not read certificate from classpath", ex);
        }
    }

    /**
     * Load private key from classpath.
     *
     * @param filename Name of the key file. Suffix should be .key
     * @return PrivateKey
     */
    public PrivateKey getKeyFromClassPath(String filename) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        InputStream stream = loader.getResourceAsStream("certificates/" + filename);
        if (stream == null) {
            throw new CertificateException(
                "Could not read private key from classpath:" + "certificates/" + filename
            );
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        try {
            Security.addProvider(new BouncyCastleProvider());
            PEMParser pp = new PEMParser(br);
            PEMKeyPair pemKeyPair = (PEMKeyPair) pp.readObject();
            KeyPair kp = new JcaPEMKeyConverter().getKeyPair(pemKeyPair);
            pp.close();
            return kp.getPrivate();
        } catch (IOException ex) {
            throw new CertificateException("Could not read private key from classpath", ex);
        }
    }

    /**
     * Generates new X.509 Certificate
     *
     * @return X509Certificate
     */
    private X509Certificate generateCertificate(SubjectData subjectData, IssuerData issuerData,
                                                QCStatement statement) {
        JcaContentSignerBuilder builder = new JcaContentSignerBuilder("SHA256WithRSAEncryption");

        ContentSigner contentSigner;

        X509v3CertificateBuilder certGen = new JcaX509v3CertificateBuilder(issuerData.getX500name(),
                                                                           new BigInteger(subjectData.getSerialNumber().toString()), subjectData.getStartDate(),
                                                                           subjectData.getEndDate(),
                                                                           subjectData.getX500name(), subjectData.getPublicKey());

        JcaX509CertificateConverter certConverter;

        try {
            contentSigner = builder.build(issuerData.getPrivateKey());
            certGen.addExtension(Extension.qCStatements, false, statement);

            X509CertificateHolder certHolder = certGen.build(contentSigner);

            certConverter = new JcaX509CertificateConverter();

            return certConverter.getCertificate(certHolder);
        } catch (Exception ex) {
            throw new CertificateException("Could not create certificate", ex);
        }
    }

    static String exportToString(Object obj) {
        try (StringWriter writer = new StringWriter(); JcaPEMWriter pemWriter = new JcaPEMWriter(
            writer)) {
            pemWriter.writeObject(obj);
            pemWriter.flush();
            return writer.toString().replaceAll("\n", "");
        } catch (IOException ex) {
            throw new CertificateException("Could not export certificate", ex);
        }
    }

    private static DERSequence createQcInfo(RolesOfPsp rolesOfPsp, NcaName ncaName, NcaId ncaId) {
        return new DERSequence(new ASN1Encodable[]{rolesOfPsp, ncaName, ncaId});
    }

    /**
     * Create a new base64 encoded X509 certificate for authentication at the XS2A API with the
     * corresponding private key and meta data.
     *
     * @param certificateRequest data needed for certificate generation
     * @return CertificateResponse base64 encoded cert + private key
     */
    public CertificateResponse createCertificate(CertificateRequest certificateRequest) {
        SubjectData subjectData = generateSubjectData(certificateRequest);
        QCStatement qcStatement = generateQcStatement(certificateRequest);

        X509Certificate cert = generateCertificate(subjectData, issuerData, qcStatement);

        return CertificateResponse.builder()
                   .privateKey(exportToString(subjectData.getPrivateKey()))
                   .encodedCert(exportToString(cert))
                   .build();
    }


    private QCStatement generateQcStatement(CertificateRequest certificateRequest) {

        NcaName ncaName = getNcaNameFromIssuerData();
        NcaId ncaId = getNcaIdFromIssuerData();
        ASN1Encodable qcStatementInfo = createQcInfo(
            RolesOfPsp.fromCertificateRequest(certificateRequest), ncaName, ncaId
        );

        return new QCStatement(ETSI_QC_STATEMENT, qcStatementInfo);
    }

    private NcaName getNcaNameFromIssuerData() {
        return new NcaName(IETFUtils.valueToString(
            issuerData.getX500name().getRDNs(BCStyle.O)[0]
                .getFirst().getValue())
        );
    }

    private NcaId getNcaIdFromIssuerData() {
        // TODO: map NcaName to NcaShortName -> dynamic generation?
        String country = IETFUtils
                             .valueToString(issuerData.getX500name().getRDNs(BCStyle.C)[0].getFirst().getValue());

        return new NcaId(country + "-" + NCA_SHORT_NAME);
    }

    private SubjectData generateSubjectData(CertificateRequest cerData) {

        X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        builder.addRDN(BCStyle.O, cerData.getOrganizationName());

        builder.addRDN(BCStyle.CN, "");

        if (cerData.getDomainComponent() != null) {
            builder.addRDN(BCStyle.DC, cerData.getDomainComponent());
        }
        if (cerData.getOrganizationUnit() != null) {
            builder.addRDN(BCStyle.OU, cerData.getOrganizationUnit());
        }
        if (cerData.getCountryName() != null) {
            builder.addRDN(BCStyle.C, cerData.getCountryName());
        }
        if (cerData.getStateOrProvinceName() != null) {
            builder.addRDN(BCStyle.ST, cerData.getStateOrProvinceName());
        }
        if (cerData.getLocalityName() != null) {
            builder.addRDN(BCStyle.L, cerData.getLocalityName());
        }

        builder.addRDN(BCStyle.ORGANIZATION_IDENTIFIER,
                       "PSD" + getNcaIdFromIssuerData() + "-" + cerData.getAuthorizationNumber());

        Date expiration = Date.from(
            LocalDate.now().plusDays(cerData.getValidity()).atStartOfDay(ZoneOffset.UTC).toInstant()
        );
        KeyPair keyPairSubject = generateKeyPair();
        Random rand = new Random();
        Integer serialNumber = rand.nextInt(Integer.MAX_VALUE);
        return new SubjectData(
            keyPairSubject.getPrivate(), keyPairSubject.getPublic(), builder.build(),
            serialNumber, new Date(), expiration
        );
    }

    private KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
            keyGen.initialize(2048, random);
            return keyGen.generateKeyPair();
        } catch (GeneralSecurityException ex) {
            throw new CertificateException("Could not generate key pair", ex);
        }
    }

    private IssuerData generateIssuerData() {
        IssuerData issuerData = new IssuerData();

        X509Certificate cert = getCertificateFromClassPath();

        log.debug("Source for issuer data: {} from {}", cert, ISSUER_CERTIFICATE);

        try {
            issuerData.setX500name(new JcaX509CertificateHolder(cert).getSubject());
        } catch (CertificateEncodingException ex) {
            throw new CertificateException("Could not read issuer data from certificate", ex);
        }

        PrivateKey privateKey = getKeyFromClassPath(ISSUER_PRIVATE_KEY);
        issuerData.setPrivateKey(privateKey);

        return issuerData;
    }

    private static class RolesOfPsp extends DERSequence {

        static RolesOfPsp fromCertificateRequest(CertificateRequest certificateRequest) {
            List<RoleOfPsp> roles = new ArrayList<>();

            if (certificateRequest.getRoles().contains(PspRole.AISP)) {
                roles.add(RoleOfPsp.PSP_AI);
            }

            if (certificateRequest.getRoles().contains(PspRole.PISP)) {
                roles.add(RoleOfPsp.PSP_PI);
            }

            if (certificateRequest.getRoles().contains(PspRole.PIISP)) {
                roles.add(RoleOfPsp.PSP_IC);
            }

            return new RolesOfPsp(roles);
        }

        RolesOfPsp(List<RoleOfPsp> roles) {
            super(roles.toArray(new RoleOfPsp[]{}));
        }
    }

    private static class RoleOfPsp extends DERSequence {

        static final RoleOfPsp PSP_PI = new RoleOfPsp(RoleOfPspOid.ID_PSD_2_ROLE_PSP_PI,
                                                      RoleOfPspName.PSP_PI);
        static final RoleOfPsp PSP_AI = new RoleOfPsp(RoleOfPspOid.ID_PSD_2_ROLE_PSP_AI,
                                                      RoleOfPspName.PSP_AI);
        static final RoleOfPsp PSP_IC = new RoleOfPsp(RoleOfPspOid.ROLE_OF_PSP_OID,
                                                      RoleOfPspName.PSP_IC);

        private RoleOfPsp(RoleOfPspOid roleOfPspOid, RoleOfPspName roleOfPspName) {
            super(new ASN1Encodable[]{roleOfPspOid, roleOfPspName});
        }
    }

    private static class RoleOfPspName extends DERUTF8String {

        // TODO we do not support the ASPSP role (PSP_AS) right now
        static final RoleOfPspName PSP_PI = new RoleOfPspName("PSP_PI");
        static final RoleOfPspName PSP_AI = new RoleOfPspName("PSP_AI");
        static final RoleOfPspName PSP_IC = new RoleOfPspName("PSP_IC");

        private RoleOfPspName(String string) {
            super(string);
        }
    }

    private static class RoleOfPspOid extends ASN1ObjectIdentifier {

        static final ASN1ObjectIdentifier ETSI_PSD_2_ROLES = new ASN1ObjectIdentifier(
            "0.4.0.19495.1");
        static final RoleOfPspOid ID_PSD_2_ROLE_PSP_PI = new RoleOfPspOid(
            ETSI_PSD_2_ROLES.branch("2"));
        static final RoleOfPspOid ID_PSD_2_ROLE_PSP_AI = new RoleOfPspOid(
            ETSI_PSD_2_ROLES.branch("3"));
        static final RoleOfPspOid ROLE_OF_PSP_OID = new RoleOfPspOid(
            ETSI_PSD_2_ROLES.branch("4"));

        RoleOfPspOid(ASN1ObjectIdentifier identifier) {
            super(identifier.getId());
        }
    }
}
