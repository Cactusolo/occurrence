package org.gbif.occurrence.ws.client.mock;

import org.gbif.api.model.occurrence.Occurrence;
import org.gbif.api.model.occurrence.VerbatimOccurrence;
import org.gbif.api.service.occurrence.OccurrenceService;
import org.gbif.api.vocabulary.BasisOfRecord;
import org.gbif.api.vocabulary.Kingdom;
import org.gbif.dwc.terms.DwcTerm;
import org.gbif.dwc.terms.Term;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;


public class OccurrencePersistenceMockService implements OccurrenceService {

  protected static final List<UUID> DATASETS = Lists.newArrayList();
  private static final String UUID_PREFIX = "111aaa11-0000-1111-2222-f5f5f5d8888";

  static {
    for (int i = 0; i < 10; i++) {
      DATASETS.add(UUID.fromString(UUID_PREFIX + i));
    }
  }

  @Override
  public Occurrence get(Integer key) {
    if (key == null || key < 1 || key >= 1000000) {
      return null;
    }

    Occurrence occ = new Occurrence();
    occ.setKey(key);
    occ.setDatasetKey(DATASETS.get(key % 10));

    int ord = key % BasisOfRecord.values().length;
    occ.setBasisOfRecord(BasisOfRecord.values()[ord]);
    occ.setYear(1800 + (key % 200));
    occ.setElevation(key % 2000d);
    Map<Term, String> fields = occ.getVerbatimFields();
    fields.put(DwcTerm.catalogNumber, "cat-" + key);
    occ.setVerbatimFields(fields);
    occ.setMonth(1 + (key % 12));

    ord = key % Kingdom.values().length;
    Kingdom k = Kingdom.values()[ord];
    occ.setKingdomKey(k.nubUsageID());
    occ.setKingdom(StringUtils.capitalize(k.name().toLowerCase()));

    return occ;
  }

  @Override
  public VerbatimOccurrence getVerbatim(Integer key) {
    return new VerbatimOccurrence();
  }

  @Override
  public String getFragment(int i) {
    return "<record>mock parsing</record>";
  }
}
