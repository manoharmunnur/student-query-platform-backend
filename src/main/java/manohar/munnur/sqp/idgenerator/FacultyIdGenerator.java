package manohar.munnur.sqp.idgenerator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.time.Year;
import java.util.List;

public class FacultyIdGenerator implements IdentifierGenerator {

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {

        String currentYear = String.valueOf(Year.now().getValue());
        String prefix = currentYear + "F";

        String query = "SELECT s.id FROM Faculty s WHERE s.id LIKE :pattern ORDER BY s.id DESC";

        List<String> idList = session.createQuery(query, String.class)
                .setParameter("pattern", prefix + "%")
                .setMaxResults(1)
                .getResultList();

        int nextSequence = 1;

        if (!idList.isEmpty()) {
            String lastId = idList.get(0);
            int lastNumber = Integer.parseInt(lastId.substring(prefix.length()));
            nextSequence = lastNumber + 1;
        }

        return prefix + String.format("%04d", nextSequence);
    }
}
