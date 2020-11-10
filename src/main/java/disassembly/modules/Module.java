package disassembly.modules;

import disassembly.InvalidOpCodeException;
import disassembly.WASMOpCode;
import disassembly.modules.sections.Section;
import disassembly.modules.sections.code.CodeSection;
import disassembly.modules.sections.custom.CustomSection;
import disassembly.modules.sections.custom.CustomSectionFactory;
import disassembly.modules.sections.data.DataSection;
import disassembly.modules.sections.element.ElementSection;
import disassembly.modules.sections.export.ExportSection;
import disassembly.modules.sections.function.FunctionSection;
import disassembly.modules.sections.global.GlobalSection;
import disassembly.modules.sections.imprt.ImportSection;
import disassembly.modules.sections.memory.MemorySection;
import disassembly.modules.sections.start.StartSection;
import disassembly.modules.sections.table.TableSection;
import disassembly.modules.sections.type.TypeSection;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class Module extends WASMOpCode {

    private Magic magic;
    private Version version;

    private TypeSection typeSection;
    private ImportSection importSection;
    private FunctionSection functionSection;
    private TableSection tableSection;
    private MemorySection memorySection;
    private GlobalSection globalSection;
    private ExportSection exportSection;
    private StartSection startSection;
    private ElementSection elementSection;
    private CodeSection codeSection;
    private DataSection dataSection;

    private List<List<CustomSection>> customSectionsList;

    public Module(BufferedInputStream in) throws IOException, InvalidOpCodeException {
        customSectionsList = new ArrayList<>();

        magic = new Magic(in);
        version = new Version(in);

        disassembleCustomSections(in);
        typeSection = isNextSection(in, 1) ? new TypeSection(in) : null;
        disassembleCustomSections(in);
        importSection = isNextSection(in, 2) ? new ImportSection(in) : null;
        disassembleCustomSections(in);
        functionSection = isNextSection(in, 3) ? new FunctionSection(in) : null;
        disassembleCustomSections(in);
        tableSection = isNextSection(in, 4) ? new TableSection(in) : null;
        disassembleCustomSections(in);
        memorySection = isNextSection(in, 5) ? new MemorySection(in) : null;
        disassembleCustomSections(in);
        globalSection = isNextSection(in , 6) ? new GlobalSection(in) : null;
        disassembleCustomSections(in);
        exportSection = isNextSection(in, 7) ? new ExportSection(in) : null;
        disassembleCustomSections(in);
        startSection = isNextSection(in, 8) ? new StartSection(in) : null;
        disassembleCustomSections(in);
        elementSection = isNextSection(in, 9) ? new ElementSection(in) : null;
        disassembleCustomSections(in);
        codeSection = isNextSection(in, 10) ? new CodeSection(in) : null;
        disassembleCustomSections(in);
        dataSection = isNextSection(in, 11) ? new DataSection(in) : null;
        disassembleCustomSections(in);

    }

    public Module(Magic magic, Version version, TypeSection typeSection, ImportSection importSection, FunctionSection functionSection, TableSection tableSection, MemorySection memorySection, GlobalSection globalSection, ExportSection exportSection, StartSection startSection, ElementSection elementSection, CodeSection codeSection, DataSection dataSection) {
        this.magic = magic;
        this.version = version;
        this.typeSection = typeSection;
        this.importSection = importSection;
        this.functionSection = functionSection;
        this.tableSection = tableSection;
        this.memorySection = memorySection;
        this.globalSection = globalSection;
        this.exportSection = exportSection;
        this.startSection = startSection;
        this.elementSection = elementSection;
        this.codeSection = codeSection;
        this.dataSection = dataSection;

        this.customSectionsList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            customSectionsList.add(new ArrayList<>());
        }
    }

    public Module(TypeSection typeSection, ImportSection importSection, FunctionSection functionSection, TableSection tableSection, MemorySection memorySection, GlobalSection globalSection, ExportSection exportSection, StartSection startSection, ElementSection elementSection, CodeSection codeSection, DataSection dataSection, List<List<CustomSection>> customSectionsList) {
        this(new Magic(), new Version(), typeSection, importSection, functionSection, tableSection, memorySection,
                globalSection, exportSection, startSection, elementSection, codeSection, dataSection);
    }

    private void disassembleCustomSections(BufferedInputStream in) throws IOException, InvalidOpCodeException {
        List<CustomSection> customSections = new ArrayList<>();

        while (isNextSection(in, 0)) {
            customSections.add(CustomSectionFactory.get(in));
        }

        in.reset();
        customSectionsList.add(customSections);
    }

    private boolean isNextSection(BufferedInputStream in, int id) throws IOException {
        in.mark(1);
        if (in.read() == id) {
            return true;
        }
        in.reset();
        return false;
    }

    @Override
    public void assemble(OutputStream out) throws IOException, InvalidOpCodeException {
        magic.assemble(out);
        version.assemble(out);

        Section[] sections = new Section[]{typeSection, importSection, functionSection, tableSection,
        memorySection, globalSection, exportSection, startSection, elementSection, codeSection,
        dataSection};

        for (int i = 0; i < 11; i++) {
            assembleCustomSections(out, i);
            if (sections[i] != null) {
                sections[i].assemble(out);
            }
        }
        assembleCustomSections(out, 11);
    }

    private void assembleCustomSections(OutputStream out, int location) throws IOException, InvalidOpCodeException {
        for(CustomSection section : customSectionsList.get(location)) {
            section.assemble(out);
        }
    }
}
